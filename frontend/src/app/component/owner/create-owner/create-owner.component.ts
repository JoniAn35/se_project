import { Component } from '@angular/core';
import { OwnerService } from '../../../service/owner.service';
import { Router } from '@angular/router';
import { NgForm, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-create-owner',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './create-owner.component.html',
  styleUrls: ['./create-owner.component.scss']
})
export class CreateOwnerComponent {
  owner = {
    firstName: '',
    lastName: '',
    email: ''
  };

  errorMessages: string[] = [];
  isLoading = false;

  constructor(
    private ownerService: OwnerService,
    private router: Router,
    private notification: ToastrService
  ) {}

  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.errorMessages = [];
      this.isLoading = true;

      this.ownerService.create(this.owner).subscribe({
        next: (createdOwner) => {
          this.notification.success(`Owner "${createdOwner.firstName} ${createdOwner.lastName}" created successfully!`);
          this.isLoading = false;
          this.router.navigate(['/owners']);
        },
        error: (error) => {
          this.isLoading = false;
          if (error.error && error.error.errors) {
            this.errorMessages = error.error.errors;
          } else {
            this.errorMessages = [error.error?.message || 'Error creating owner'];
          }
          this.notification.error('Error creating owner');
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/owners']);
  }
}
