import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';
import { AutocompleteComponent } from 'src/app/component/autocomplete/autocomplete.component';
import { Horse, convertFromHorseToCreate } from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { Sex } from 'src/app/dto/sex';
import { HorseService } from 'src/app/service/horse.service';
import { OwnerService } from 'src/app/service/owner.service';

export enum HorseCreateEditMode {
  create,
  edit,
};

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  imports: [
    FormsModule,
    AutocompleteComponent,
    FormsModule
],
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: '',
    sex: Sex.female,
  };
  horseId: number | null = null;

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit Horse';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Save Changes';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }

  get modeIsEdit(): boolean {
    return this.mode === HorseCreateEditMode.edit;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode === HorseCreateEditMode.edit) {
      this.route.paramMap.subscribe(params => {
        const id = params.get('id');
        if (id) {
          this.horseId = parseInt(id);
          this.loadHorse(this.horseId);
        }
      });
    }
  }

  private loadHorse(id: number): void {
    this.service.getById(id).subscribe({
      next: (horse: Horse) => {
        this.horse = horse;
      },
      error: (error: any) => {
        console.error('Error loading horse', error);
        this.notification.error('Failed to load horse');
        this.router.navigate(['/horses']);
      }
    });
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }


  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      if (this.horse.description === '') {
        delete this.horse.description;
      }
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(
            convertFromHorseToCreate(this.horse)
          );
          break;
        case HorseCreateEditMode.edit:
          if (!this.horseId) {
            console.error('No horse ID for edit');
            return;
          }
          observable = this.service.update(
            this.horseId,
            convertFromHorseToCreate(this.horse)
          );
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: (data: Horse) => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: (error: any) => {
          console.error(`Error ${this.modeActionFinished === 'created' ? 'creating' : 'updating'} horse`, error);
          this.notification.error(`Failed to ${this.modeActionFinished === 'created' ? 'create' : 'update'} horse`);
        }
      });
    }
  }

  public deleteHorse(): void {
    if (!this.horseId) {
      console.error('No horse ID for delete');
      return;
    }

    if (confirm(`Are you sure you want to delete horse "${this.horse.name}"?`)) {
      this.service.delete(this.horseId).subscribe({
        next: () => {
          this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
          this.router.navigate(['/horses']);
        },
        error: (error: any) => {
          console.error('Error deleting horse', error);
          this.notification.error('Failed to delete horse');
        }
      });
    }
  }
}
