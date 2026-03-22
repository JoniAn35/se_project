import {Routes} from '@angular/router';
import {HorseCreateEditComponent, HorseCreateEditMode} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';
import {HorseDetailsComponent} from './component/horse/horse-details/horse-details.component';
import { OwnerComponent } from './component/owner/owner.component';
import { CreateOwnerComponent } from './component/owner/create-owner/create-owner.component';
import { HomeComponent } from './component/home/home.component';

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'horses', children: [
    {path: '', component: HorseComponent},
    {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.create}},
    {path: ':id/edit', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.edit}},
    {path: ':id/details', component: HorseDetailsComponent},
  ]},
  {path: 'owners', children: [
    {path: '', component: OwnerComponent},
    {path: 'create', component: CreateOwnerComponent},
  ]},
  {path: '**', redirectTo: ''},
];
