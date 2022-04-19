import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAddress, Address } from '../address.model';

import { AddressService } from './address.service';

describe('Address Service', () => {
  let service: AddressService;
  let httpMock: HttpTestingController;
  let elemDefault: IAddress;
  let expectedResult: IAddress | IAddress[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AddressService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      address1: 'AAAAAAA',
      address2: 'AAAAAAA',
      city: 'AAAAAAA',
      postcode: 'AAAAAAA',
      country: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Address', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Address()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Address', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          address1: 'BBBBBB',
          address2: 'BBBBBB',
          city: 'BBBBBB',
          postcode: 'BBBBBB',
          country: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Address', () => {
      const patchObject = Object.assign({}, new Address());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Address', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          address1: 'BBBBBB',
          address2: 'BBBBBB',
          city: 'BBBBBB',
          postcode: 'BBBBBB',
          country: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Address', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAddressToCollectionIfMissing', () => {
      it('should add a Address to an empty array', () => {
        const address: IAddress = { id: 'ABC' };
        expectedResult = service.addAddressToCollectionIfMissing([], address);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(address);
      });

      it('should not add a Address to an array that contains it', () => {
        const address: IAddress = { id: 'ABC' };
        const addressCollection: IAddress[] = [
          {
            ...address,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, address);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Address to an array that doesn't contain it", () => {
        const address: IAddress = { id: 'ABC' };
        const addressCollection: IAddress[] = [{ id: 'CBA' }];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, address);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(address);
      });

      it('should add only unique Address to an array', () => {
        const addressArray: IAddress[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'cf90aa77-3cdd-42e9-8652-bedcaedb2201' }];
        const addressCollection: IAddress[] = [{ id: 'ABC' }];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, ...addressArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const address: IAddress = { id: 'ABC' };
        const address2: IAddress = { id: 'CBA' };
        expectedResult = service.addAddressToCollectionIfMissing([], address, address2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(address);
        expect(expectedResult).toContain(address2);
      });

      it('should accept null and undefined values', () => {
        const address: IAddress = { id: 'ABC' };
        expectedResult = service.addAddressToCollectionIfMissing([], null, address, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(address);
      });

      it('should return initial array if no Address is added', () => {
        const addressCollection: IAddress[] = [{ id: 'ABC' }];
        expectedResult = service.addAddressToCollectionIfMissing(addressCollection, undefined, null);
        expect(expectedResult).toEqual(addressCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
