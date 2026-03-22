import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Horse, HorseCreate } from '../dto/horse';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
  }

  /**
   * Get a horse by its ID
   *
   * @param id the ID of the horse to fetch
   * @return observable of the horse
   */
  getById(id: number): Observable<Horse> {
    return this.http.get<Horse>(`${baseUri}/${id}`);
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: HorseCreate
  ): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  /**
   * Update an existing horse in the system.
   *
   * @param id the ID of the horse to update
   * @param horse the updated horse data
   * @return an Observable for the updated horse
   */
  update(id: number, horse: HorseCreate): Observable<Horse> {
    return this.http.put<Horse>(
      `${baseUri}/${id}`,
      horse
    );
  }

  /**
   * Delete a horse from the system.
   *
   * @param id the ID of the horse to delete
   * @return an Observable that completes when deletion is done
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${baseUri}/${id}`);
  }
}
