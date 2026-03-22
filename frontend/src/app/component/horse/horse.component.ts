import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AutocompleteComponent } from 'src/app/component/autocomplete/autocomplete.component';
import { HorseService } from 'src/app/service/horse.service';
import { Horse, HorseSearch } from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { ConfirmDeleteDialogComponent } from 'src/app/component/confirm-delete-dialog/confirm-delete-dialog.component';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  imports: [
    RouterLink,
    FormsModule,
    AutocompleteComponent,
    ConfirmDeleteDialogComponent
],
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  horses: Horse[] = [];
  bannerError: string | null = null;
  horseForDeletion: Horse | undefined;

  // Search form fields
  searchHorseName: string = '';
  searchHorseDescription: string = '';
  searchHorseDateOfBirth: string = '';
  searchHorseSex: string = '';
  searchHorseOwnerId: number | null = null;

  constructor(
    private service: HorseService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    // Do NOT load horses on init - start with empty table
  }

  /**
   * Load all horses (Show All button)
   */
  showAllHorses() {
    this.searchHorseName = '';
    this.searchHorseDescription = '';
    this.searchHorseDateOfBirth = '';
    this.searchHorseSex = '';
    this.searchHorseOwnerId = null;
    this.loadHorses();
  }

  /**
   * Clear search and table
   */
  clearSearch() {
    this.searchHorseName = '';
    this.searchHorseDescription = '';
    this.searchHorseDateOfBirth = '';
    this.searchHorseSex = '';
    this.searchHorseOwnerId = null;
    this.horses = [];
  }

  /**
   * Search horses based on current form values
   */
  searchHorses() {
    const criteria: HorseSearch = {};

    if (this.searchHorseName?.trim()) {
      criteria.name = this.searchHorseName.trim();
    }
    if (this.searchHorseDescription?.trim()) {
      criteria.description = this.searchHorseDescription.trim();
    }
    if (this.searchHorseDateOfBirth?.trim()) {
      criteria.dateOfBirth = this.searchHorseDateOfBirth.trim();
    }
    if (this.searchHorseSex?.trim()) {
      criteria.sex = this.searchHorseSex as any;
    }
    if (this.searchHorseOwnerId) {
      criteria.ownerId = this.searchHorseOwnerId;
    }

    this.service.search(criteria).subscribe({
      next: data => {
        this.horses = data;
        if (data.length === 0) {
          this.notification.info('No horses found matching your criteria');
        }
      },
      error: error => {
        console.error('Error searching horses', error);
        this.bannerError = 'Could not search horses: ' + error.message;
        this.notification.error('Search failed');
      }
    });
  }

  /**
   * Load all horses from the system
   */
  private loadHorses() {
    this.service.getAll().subscribe({
      next: data => {
        this.horses = data;
      },
      error: error => {
        console.error('Error fetching horses', error);
        this.bannerError = 'Could not fetch horses: ' + error.message;
        const errorMessage = error.status === 0
          ? 'Is the backend up?'
          : error.message.message;
        this.notification.error(errorMessage, 'Could Not Fetch Horses');
      }
    });
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }


  public deleteHorse(horse: Horse | null): void {
    if (!horse || !horse.id) {
      return;
    }

    this.service.delete(horse.id).subscribe({
      next: () => {
        this.notification.success(`Horse ${horse.name} successfully deleted.`);
        this.showAllHorses();
      },
      error: (error: any) => {
        console.error('Error deleting horse', error);
        this.notification.error('Failed to delete horse');
      }
    });
  }

  /**
   * Called when owner is selected from autocomplete
   */
  onOwnerSelect(owner: Owner | null) {
    this.searchHorseOwnerId = owner?.id || null;
  }
}
