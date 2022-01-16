import { IFatturePassivo } from 'app/entities/fatture-passivo/fatture-passivo.model';

export interface IFornitore {
  id?: number;
  nomeFornitore?: string;
  indirizzo?: string | null;
  tipo?: string | null;
  fatturas?: IFatturePassivo[] | null;
}

export class Fornitore implements IFornitore {
  constructor(
    public id?: number,
    public nomeFornitore?: string,
    public indirizzo?: string | null,
    public tipo?: string | null,
    public fatturas?: IFatturePassivo[] | null
  ) {}
}

export function getFornitoreIdentifier(fornitore: IFornitore): number | undefined {
  return fornitore.id;
}
