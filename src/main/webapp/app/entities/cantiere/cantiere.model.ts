import { IFattureAttivo } from 'app/entities/fatture-attivo/fatture-attivo.model';
import { IFatturePassivo } from 'app/entities/fatture-passivo/fatture-passivo.model';

export interface ICantiere {
  id?: number;
  nomeCantiere?: string;
  indirizzo?: string | null;
  afatturas?: IFattureAttivo[] | null;
  pfatturas?: IFatturePassivo[] | null;
}

export class Cantiere implements ICantiere {
  constructor(
    public id?: number,
    public nomeCantiere?: string,
    public indirizzo?: string | null,
    public afatturas?: IFattureAttivo[] | null,
    public pfatturas?: IFatturePassivo[] | null
  ) {}
}

export function getCantiereIdentifier(cantiere: ICantiere): number | undefined {
  return cantiere.id;
}
