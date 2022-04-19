import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';

export interface IProduct {
  id?: string;
  title?: string;
  keywords?: string | null;
  description?: string | null;
  rating?: number | null;
  dateAdded?: dayjs.Dayjs | null;
  dateModified?: dayjs.Dayjs | null;
  categories?: ICategory[] | null;
}

export class Product implements IProduct {
  constructor(
    public id?: string,
    public title?: string,
    public keywords?: string | null,
    public description?: string | null,
    public rating?: number | null,
    public dateAdded?: dayjs.Dayjs | null,
    public dateModified?: dayjs.Dayjs | null,
    public categories?: ICategory[] | null
  ) {}
}

export function getProductIdentifier(product: IProduct): string | undefined {
  return product.id;
}
