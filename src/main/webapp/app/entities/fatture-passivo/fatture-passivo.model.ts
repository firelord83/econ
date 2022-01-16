import dayjs from 'dayjs/esm';
import { IFornitore } from 'app/entities/fornitore/fornitore.model';
import { ICantiere } from 'app/entities/cantiere/cantiere.model';
import { Stato } from 'app/entities/enumerations/stato.model';

export interface IFatturePassivo {
  id?: number;
  numeroFattura?: number;
  ragSociale?: string | null;
  nomeFornitore?: string | null;
  imponibile?: number | null;
  iva?: number | null;
  stato?: Stato | null;
  dataEmissione?: dayjs.Dayjs | null;
  dataPagamento?: dayjs.Dayjs | null;
  fornitore?: IFornitore | null;
  cantiere?: ICantiere | null;
}

export class FatturePassivo implements IFatturePassivo {
  constructor(
    public id?: number,
    public numeroFattura?: number,
    public ragSociale?: string | null,
    public nomeFornitore?: string | null,
    public imponibile?: number | null,
    public iva?: number | null,
    public stato?: Stato | null,
    public dataEmissione?: dayjs.Dayjs | null,
    public dataPagamento?: dayjs.Dayjs | null,
    public fornitore?: IFornitore | null,
    public cantiere?: ICantiere | null
  ) {}
}

export function getFatturePassivoIdentifier(fatturePassivo: IFatturePassivo): number | undefined {
  return fatturePassivo.id;
}
