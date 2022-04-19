import { ICustomer } from 'app/entities/customer/customer.model';

export interface IAddress {
  id?: string;
  address1?: string | null;
  address2?: string | null;
  city?: string | null;
  postcode?: string;
  country?: string;
  customer?: ICustomer | null;
}

export class Address implements IAddress {
  constructor(
    public id?: string,
    public address1?: string | null,
    public address2?: string | null,
    public city?: string | null,
    public postcode?: string,
    public country?: string,
    public customer?: ICustomer | null
  ) {}
}

export function getAddressIdentifier(address: IAddress): string | undefined {
  return address.id;
}
