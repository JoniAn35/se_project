import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HorseService } from '../../service/horse.service';
import { OwnerService } from '../../service/owner.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HorseCreate } from '../../dto/horse';
import { Owner } from '../../dto/owner';
import { Sex } from '../../dto/sex';

@Component({
  selector: 'app-horse-create',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './horse-create.component.html',
  styleUrls: ['./horse-create.component.scss']
})
export class HorseCreateComponent implements OnInit {
  form: FormGroup;
  owners: Owner[] = [];
  sexOptions: Sex[] = [Sex.male, Sex.female];
  submitted = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private horseService: HorseService,
    private ownerService: OwnerService,
    private router: Router
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]],
      description: [''],
      dateOfBirth: ['', Validators.required],
      sex: ['', Validators.required],
      ownerId: [null]
    });
  }

  ngOnInit(): void {
    this.loadOwners();
  }

  loadOwners(): void {
    this.ownerService.searchByName('', 1000).subscribe({
      next: (owners: Owner[]) => {
        this.owners = owners;
      },
      error: (err: any) => console.error('Error loading owners', err)
    });
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = null;

    if (this.form.invalid) {
      return;
    }

    const formData = this.form.value;
    const horseData: HorseCreate = {
      name: formData.name,
      description: formData.description || undefined,
      dateOfBirth: formData.dateOfBirth,
      sex: formData.sex,
      ownerId: formData.ownerId || undefined
    };

    this.horseService.create(horseData).subscribe({
      next: (horse) => {
        console.log('Horse created successfully', horse);
        this.router.navigate(['/horses', horse.id]);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error creating horse';
        console.error('Error creating horse', err);
      }
    });
  }

  get name() {
    return this.form.get('name');
  }

  get dateOfBirth() {
    return this.form.get('dateOfBirth');
  }

  get sex() {
    return this.form.get('sex');
  }
}
