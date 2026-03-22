import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {
  private apiUrl = 'http://your-backend-api/owners';

  constructor(private http: HttpClient) {}

  // Create method to post new owner to backend
  create(ownerData: any): Observable<any> {
    return this.http.post(this.apiUrl, ownerData);
  }
}
