import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IAddress, getAddressIdentifier } from '../address.model';

export type EntityResponseType = HttpResponse<IAddress>;
export type EntityArrayResponseType = HttpResponse<IAddress[]>;

@Injectable({ providedIn: 'root' })
export class AddressService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/addresses');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/addresses');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(address: IAddress): Observable<EntityResponseType> {
    return this.http.post<IAddress>(this.resourceUrl, address, { observe: 'response' });
  }

  update(address: IAddress): Observable<EntityResponseType> {
    return this.http.put<IAddress>(`${this.resourceUrl}/${getAddressIdentifier(address) as string}`, address, { observe: 'response' });
  }

  partialUpdate(address: IAddress): Observable<EntityResponseType> {
    return this.http.patch<IAddress>(`${this.resourceUrl}/${getAddressIdentifier(address) as string}`, address, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IAddress>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddress[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAddress[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAddressToCollectionIfMissing(addressCollection: IAddress[], ...addressesToCheck: (IAddress | null | undefined)[]): IAddress[] {
    const addresses: IAddress[] = addressesToCheck.filter(isPresent);
    if (addresses.length > 0) {
      const addressCollectionIdentifiers = addressCollection.map(addressItem => getAddressIdentifier(addressItem)!);
      const addressesToAdd = addresses.filter(addressItem => {
        const addressIdentifier = getAddressIdentifier(addressItem);
        if (addressIdentifier == null || addressCollectionIdentifiers.includes(addressIdentifier)) {
          return false;
        }
        addressCollectionIdentifiers.push(addressIdentifier);
        return true;
      });
      return [...addressesToAdd, ...addressCollection];
    }
    return addressCollection;
  }
}
