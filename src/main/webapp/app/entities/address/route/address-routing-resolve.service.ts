import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAddress, Address } from '../address.model';
import { AddressService } from '../service/address.service';

@Injectable({ providedIn: 'root' })
export class AddressRoutingResolveService implements Resolve<IAddress> {
  constructor(protected service: AddressService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAddress> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((address: HttpResponse<Address>) => {
          if (address.body) {
            return of(address.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Address());
  }
}
