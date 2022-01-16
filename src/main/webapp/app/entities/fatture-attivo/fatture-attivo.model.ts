import dayjs from 'dayjs/esm';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ICantiere } from 'app/entities/cantiere/cantiere.model';
import { Stato } from 'app/entities/enumerations/stato.model';

export interface IFattureAttivo {
  id?: number;
  numeroFattura?: number;
  ragSociale?: string | null;
  nomeCliente?: string | null;
  imponibile?: number | null;
  iva?: number | null;
  stato?: Stato | null;
  dataEmissione?: dayjs.Dayjs | null;
  dataPagamento?: dayjs.Dayjs | null;
  cliente?: ICliente | null;
  cantiere?: ICantiere | null;
}

export class FattureAttivo implements IFattureAttivo {
  constructor(
    public id?: number,
    public numeroFattura?: number,
    public ragSociale?: string | null,
    public nomeCliente?: string | null,
    public imponibile?: number | null,
    public iva?: number | null,
    public stato?: Stato | null,
    public dataEmissione?: dayjs.Dayjs | null,
    public dataPagamento?: dayjs.Dayjs | null,
    public cliente?: ICliente | null,
    public cantiere?: ICantiere | null
  ) {}
}

export function getFattureAttivoIdentifier(fattureAttivo: IFattureAttivo): number | undefined {
  return fattureAttivo.id;
}
