import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Horse } from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { HorseService } from 'src/app/service/horse.service';

@Component({
  selector: 'app-horse-details',
  templateUrl: './horse-details.component.html',
  styleUrls: ['./horse-details.component.scss']
})
export class HorseDetailsComponent implements OnInit {
  horse: Horse | null = null;
  horseId: number | null = null;
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private service: HorseService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.horseId = parseInt(id);
        this.loadHorse(this.horseId);
      }
    });
  }

  private loadHorse(id: number): void {
    this.loading = true;
    this.error = null;
    this.service.getById(id).subscribe({
      next: (horse: Horse) => {
        this.horse = horse;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading horse', error);
        this.error = 'Failed to load horse details';
        this.loading = false;
        this.notification.error('Failed to load horse');
      }
    });
  }

  ownerName(owner: Owner | null | undefined): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : 'No owner';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  goToEdit(): void {
    if (this.horseId) {
      this.router.navigate(['/horses', this.horseId, 'edit']);
    }
  }

  deleteHorse(): void {
    if (!this.horse || !this.horseId) {
      return;
    }

    if (confirm(`Are you sure you want to delete horse "${this.horse.name}"?`)) {
      this.service.delete(this.horseId).subscribe({
        next: () => {
          this.notification.success(`Horse ${this.horse!.name} successfully deleted.`);
          this.router.navigate(['/horses']);
        },
        error: (error: any) => {
          console.error('Error deleting horse', error);
          this.notification.error('Failed to delete horse');
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/horses']);
  }

  viewParent(parentId: number | undefined): void {
    if (parentId) {
      this.router.navigate(['/horses', parentId, 'details']);
    }
  }
}
