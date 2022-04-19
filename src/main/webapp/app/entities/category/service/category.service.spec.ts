import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { CategoryStatus } from 'app/entities/enumerations/category-status.model';
import { ICategory, Category } from '../category.model';

import { CategoryService } from './category.service';

describe('Category Service', () => {
  let service: CategoryService;
  let httpMock: HttpTestingController;
  let elemDefault: ICategory;
  let expectedResult: ICategory | ICategory[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CategoryService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 'AAAAAAA',
      description: 'AAAAAAA',
      sortOrder: 0,
      dateAdded: currentDate,
      dateModified: currentDate,
      status: CategoryStatus.AVAILABLE,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dateAdded: currentDate.format(DATE_FORMAT),
          dateModified: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Category', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
          dateAdded: currentDate.format(DATE_FORMAT),
          dateModified: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateAdded: currentDate,
          dateModified: currentDate,
        },
        returnedFromService
      );

      service.create(new Category()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Category', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
          sortOrder: 1,
          dateAdded: currentDate.format(DATE_FORMAT),
          dateModified: currentDate.format(DATE_FORMAT),
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateAdded: currentDate,
          dateModified: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Category', () => {
      const patchObject = Object.assign(
        {
          sortOrder: 1,
          dateModified: currentDate.format(DATE_FORMAT),
        },
        new Category()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dateAdded: currentDate,
          dateModified: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Category', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
          sortOrder: 1,
          dateAdded: currentDate.format(DATE_FORMAT),
          dateModified: currentDate.format(DATE_FORMAT),
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dateAdded: currentDate,
          dateModified: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Category', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCategoryToCollectionIfMissing', () => {
      it('should add a Category to an empty array', () => {
        const category: ICategory = { id: 'ABC' };
        expectedResult = service.addCategoryToCollectionIfMissing([], category);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(category);
      });

      it('should not add a Category to an array that contains it', () => {
        const category: ICategory = { id: 'ABC' };
        const categoryCollection: ICategory[] = [
          {
            ...category,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCategoryToCollectionIfMissing(categoryCollection, category);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Category to an array that doesn't contain it", () => {
        const category: ICategory = { id: 'ABC' };
        const categoryCollection: ICategory[] = [{ id: 'CBA' }];
        expectedResult = service.addCategoryToCollectionIfMissing(categoryCollection, category);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(category);
      });

      it('should add only unique Category to an array', () => {
        const categoryArray: ICategory[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '1c9235d0-2095-4370-86af-9587dfee4552' }];
        const categoryCollection: ICategory[] = [{ id: 'ABC' }];
        expectedResult = service.addCategoryToCollectionIfMissing(categoryCollection, ...categoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const category: ICategory = { id: 'ABC' };
        const category2: ICategory = { id: 'CBA' };
        expectedResult = service.addCategoryToCollectionIfMissing([], category, category2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(category);
        expect(expectedResult).toContain(category2);
      });

      it('should accept null and undefined values', () => {
        const category: ICategory = { id: 'ABC' };
        expectedResult = service.addCategoryToCollectionIfMissing([], null, category, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(category);
      });

      it('should return initial array if no Category is added', () => {
        const categoryCollection: ICategory[] = [{ id: 'ABC' }];
        expectedResult = service.addCategoryToCollectionIfMissing(categoryCollection, undefined, null);
        expect(expectedResult).toEqual(categoryCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
