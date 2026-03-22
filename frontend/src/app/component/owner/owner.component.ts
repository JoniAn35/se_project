import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OwnerService } from '../../service/owner.service';
import { Owner } from '../../dto/owner';

@Component({
  selector: 'app-owners',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './owners.component.html',
  styleUrls: ['./owners.component.scss']
})
export class OwnersComponent implements OnInit {
  owners: Owner[] = [];
  isLoading = false;

  constructor(private ownerService: OwnerService) {}

  ngOnInit(): void {
    this.loadOwners();
  }

  loadOwners(): void {
    this.isLoading = true;
    this.ownerService.searchByName('', 1000).subscribe({
      next: (data: Owner[]) => {
        this.owners = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading owners:', error);
        this.isLoading = false;
      }
    });
  }
}
