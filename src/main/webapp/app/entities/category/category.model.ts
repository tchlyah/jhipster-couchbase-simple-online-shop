import dayjs from 'dayjs/esm';
import { IProduct } from 'app/entities/product/product.model';
import { CategoryStatus } from 'app/entities/enumerations/category-status.model';

export interface ICategory {
  id?: string;
  description?: string;
  sortOrder?: number | null;
  dateAdded?: dayjs.Dayjs | null;
  dateModified?: dayjs.Dayjs | null;
  status?: CategoryStatus | null;
  parent?: ICategory | null;
  products?: IProduct[] | null;
}

export class Category implements ICategory {
  constructor(
    public id?: string,
    public description?: string,
    public sortOrder?: number | null,
    public dateAdded?: dayjs.Dayjs | null,
    public dateModified?: dayjs.Dayjs | null,
    public status?: CategoryStatus | null,
    public parent?: ICategory | null,
    public products?: IProduct[] | null
  ) {}
}

export function getCategoryIdentifier(category: ICategory): string | undefined {
  return category.id;
}
