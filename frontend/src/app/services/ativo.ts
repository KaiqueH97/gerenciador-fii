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

  // Função que pega a senha salva no navegador e coloca no "envelope"
  private getHeaders(): { headers: HttpHeaders } {
    const token = localStorage.getItem('meuTokenDeAcesso') || '';
    return {
      headers: new HttpHeaders({
        'Authorization': 'Basic ' + token
      })
    };
  }

  // --- O TESTE ---
  testarLogin(usuario: string, senha: string): Observable<any> {
    // btoa() é a função do JavaScript que embaralha a senha no formato Base64
    const token = btoa(usuario + ':' + senha); 
    const headers = new HttpHeaders({ 'Authorization': 'Basic ' + token });
    
    // Tenta bater na porta dos ativos. Se o Java deixar entrar, a senha está certa!
    return this.http.get(this.apiUrlAtivos, { headers });
  }

  // --- ATIVOS (Agora todos levam a credencial!) ---
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

  // --- DIVIDENDOS ---
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
}