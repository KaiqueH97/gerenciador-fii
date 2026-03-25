import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Ativo {
  id?: number;
  ticker: string;
  tipo: string;
  quantidadeCotas: number;
  precoMedio: number;
}

export interface Dividendo {
  id?: number;
  valor: number;
  dataPagamento: string;
}

@Injectable({
  providedIn: 'root'
})
export class AtivoService {
  public apiUrlAtivos = 'https://gerenciador-fii.onrender.com/api/ativos';
  public apiUrlDividendos = 'https://gerenciador-fii.onrender.com/api/dividendos';

  constructor(private http: HttpClient) { }

  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('meuTokenDeAcesso') || '';
    return {
      headers: new HttpHeaders({
        'Authorization': 'Basic ' + token
      })
    };
  }

  testarLogin(usuario: string, senha: string): Observable<any> {
    const token = btoa(usuario + ':' + senha); 
    const headers = new HttpHeaders({ 'Authorization': 'Basic ' + token });
    
    return this.http.get(this.apiUrlAtivos, { headers });
  }

  listarAtivos(): Observable<Ativo[]> {
    return this.http.get<Ativo[]>(this.apiUrlAtivos, this.getHeaders());
  }

  salvarAtivo(ativo: Ativo): Observable<Ativo> {
    return this.http.post<Ativo>(this.apiUrlAtivos, ativo, this.getHeaders());
  }

  atualizarAtivo(id: number, ativo: Ativo): Observable<Ativo> {
    return this.http.put<Ativo>(`${this.apiUrlAtivos}/${id}`, ativo, this.getHeaders());
  }

  excluirAtivo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrlAtivos}/${id}`, this.getHeaders());
  }

  listarTodosDividendos(): Observable<Dividendo[]> {
    return this.http.get<Dividendo[]>(this.apiUrlDividendos, this.getHeaders());
  }

  listarDividendosPorAtivo(ativoId: number): Observable<Dividendo[]> {
    return this.http.get<Dividendo[]>(`${this.apiUrlDividendos}/ativo/${ativoId}`, this.getHeaders());
  }

  salvarDividendo(ativoId: number, dividendo: Dividendo): Observable<Dividendo> {
    return this.http.post<Dividendo>(`${this.apiUrlDividendos}/ativo/${ativoId}`, dividendo, this.getHeaders());
  }

  atualizarDividendo(ativoId: number, dividendo: Dividendo): Observable<Dividendo> {
    return this.http.put<Dividendo>(`${this.apiUrlDividendos}/${dividendo.id}`, dividendo, this.getHeaders());
  }

  excluirDividendo(dividendoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrlDividendos}/${dividendoId}`, this.getHeaders());
  }

registrarUsuario(usuario: string, senha: string): Observable<any> {
    const url = 'https://gerenciador-fii.onrender.com/api/auth/register';
    
    return this.http.post(url, { username: usuario, password: senha }, { responseType: 'text' });
  }
}