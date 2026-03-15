import {Owner} from './owner';
import {Sex} from './sex';

/* In this template we use strings for representating date fields because
 * date input fields provide their value as ISO-formatted string and
 * the backend both expects and delivers the date also in this format.
 * Parsing and formatting of the date is therefore almost never necessary.
 */

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: string; // See above comment for why this is a string
  sex: Sex;
  owner?: Owner;
}

export interface HorseSearch {
  name?: string;
  // TODO fill in missing fields
}

export interface HorseCreate {
  name: string;
  description?: string;
  dateOfBirth: string;
  sex: Sex;
  ownerId?: number;
}

export function convertFromHorseToCreate(horse: Horse): HorseCreate {
  return {
    name: horse.name,
    description: horse.description,
    dateOfBirth: horse.dateOfBirth,
    sex: horse.sex,
    ownerId: horse.owner?.id,
  };
}

