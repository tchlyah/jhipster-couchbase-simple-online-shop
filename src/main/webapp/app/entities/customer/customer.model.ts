import { IAddress } from 'app/entities/address/address.model';

export interface ICustomer {
  id?: string;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  telephone?: string | null;
  addresses?: IAddress[] | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: string,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string | null,
    public telephone?: string | null,
    public addresses?: IAddress[] | null
  ) {}
}

export function getCustomerIdentifier(customer: ICustomer): string | undefined {
  return customer.id;
}
