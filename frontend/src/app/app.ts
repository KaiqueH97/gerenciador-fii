import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AtivoService, Ativo, Dividendo } from './services/ativo';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css' 
})
export class App implements OnInit {
  
  isAutenticado: boolean = false;
  usuarioLogin: string = '';
  senhaLogin: string = '';
  erroLogin: boolean = false;

  // NOVAS VARIÁVEIS DE CARREGAMENTO (UX)
  isLoadingLogin: boolean = false;
  isLoadingAcao: boolean = false;

  ativos: Ativo[] = [];
  dividendos: Dividendo[] = []; 
  ativoSelecionadoParaDividendo: Ativo | null = null;
  novoDividendo: Dividendo = { valor: 0, dataPagamento: '' };
  valorTotalInvestido: string | number = '';
  novoAtivo: Ativo = { ticker: '', tipo: 'FII', quantidadeCotas: 0, precoMedio: 0 };
  totalPatrimonio: number = 0;
  totalDividendos: number = 0;
  grafico: any; 

  constructor(private ativoService: AtivoService) {}

  ngOnInit(): void {
    const tokenSalvo = localStorage.getItem('meuTokenDeAcesso');
    if (tokenSalvo) {
      this.isAutenticado = true;
      this.iniciarSistema();
    }
  }

  fazerLogin(): void {
    this.isLoadingLogin = true; // Trava o botão e avisa que está carregando
    this.ativoService.testarLogin(this.usuarioLogin, this.senhaLogin).subscribe({
      next: () => {
        const token = btoa(this.usuarioLogin + ':' + this.senhaLogin);
        localStorage.setItem('meuTokenDeAcesso', token);
        this.isAutenticado = true;
        this.erroLogin = false;
        this.isLoadingLogin = false; // Libera o botão
        this.iniciarSistema();
      },
      error: () => {
        this.erroLogin = true;
        this.isLoadingLogin = false; // Libera o botão
      }
    });
  }

  sair(): void {
    localStorage.removeItem('meuTokenDeAcesso');
    this.isAutenticado = false;
    this.ativos = [];
    this.dividendos = [];
    this.usuarioLogin = '';
    this.senhaLogin = '';
  }

  iniciarSistema(): void {
    this.carregarAtivos();
    this.carregarTodosDividendos();
  }

  carregarAtivos(): void {
    this.ativoService.listarAtivos().subscribe(dados => {
      this.ativos = dados;
      this.calcularPatrimonio();
      setTimeout(() => this.atualizarGrafico(), 1); 
    });
  }

  carregarTodosDividendos(): void {
    this.ativoService.listarTodosDividendos().subscribe(dados => {
      this.totalDividendos = dados.reduce((soma, div) => soma + div.valor, 0);
    });
  }

  calcularPatrimonio(): void {
    this.totalPatrimonio = this.ativos.reduce((soma, ativo) => 
      soma + (ativo.quantidadeCotas * ativo.precoMedio), 0);
  }

  tratarMoeda(valor: any): number {
    if (!valor) return 0;
    return parseFloat(valor.toString().replace(',', '.'));
  }

  getPrecoMedioCalculado(): string {
    if (this.novoAtivo.quantidadeCotas > 0 && this.valorTotalInvestido) {
      let valor = this.tratarMoeda(this.valorTotalInvestido);
      if (valor > 0) return (valor / this.novoAtivo.quantidadeCotas).toFixed(2).replace('.', ',');
    }
    return '0,00';
  }

  adicionarAtivo(): void {
    let valorInvestido = this.tratarMoeda(this.valorTotalInvestido);
    if (this.novoAtivo.quantidadeCotas > 0) {
      this.novoAtivo.precoMedio = valorInvestido / this.novoAtivo.quantidadeCotas;
    }

    this.isLoadingAcao = true; // Trava o botão

    if (this.novoAtivo.id) {
      this.ativoService.atualizarAtivo(this.novoAtivo.id, this.novoAtivo).subscribe(ativoAtualizado => {
        const index = this.ativos.findIndex(a => a.id === ativoAtualizado.id);
        if (index !== -1) this.ativos[index] = ativoAtualizado;
        this.calcularPatrimonio();
        setTimeout(() => this.atualizarGrafico(), 1);
        this.limparFormulario();
        this.isLoadingAcao = false; // Libera o botão
      });
    } else {
      this.ativoService.salvarAtivo(this.novoAtivo).subscribe(ativoSalvo => {
        this.ativos.push(ativoSalvo);
        this.calcularPatrimonio();
        setTimeout(() => this.atualizarGrafico(), 1);
        this.limparFormulario();
        this.isLoadingAcao = false; // Libera o botão
      });
    }
  }

  prepararEdicao(ativo: Ativo): void {
    this.novoAtivo = { ...ativo }; 
    this.valorTotalInvestido = (ativo.quantidadeCotas * ativo.precoMedio).toFixed(2).replace('.', ','); 
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  removerAtivo(id: number | undefined): void {
    if (id) {
      const confirmar = confirm('Tem certeza que deseja apagar este ativo e TODOS os dividendos dele?');
      if (confirmar) {
        this.isLoadingAcao = true; // Trava ações
        this.ativoService.excluirAtivo(id).subscribe(() => {
          this.ativos = this.ativos.filter(a => a.id !== id);
          this.calcularPatrimonio();
          this.carregarTodosDividendos(); 
          setTimeout(() => this.atualizarGrafico(), 1);
          this.isLoadingAcao = false; // Libera ações
        });
      }
    }
  }

  limparFormulario(): void {
    this.novoAtivo = { ticker: '', tipo: 'FII', quantidadeCotas: 0, precoMedio: 0 };
    this.valorTotalInvestido = '';
  }

  abrirDividendos(ativo: Ativo): void {
    this.ativoSelecionadoParaDividendo = ativo; 
    if (ativo.id) {
      this.ativoService.listarDividendosPorAtivo(ativo.id).subscribe(dados => {
        this.dividendos = dados;
      });
    }
  }

  fecharDividendos(): void {
    this.ativoSelecionadoParaDividendo = null; 
    this.dividendos = [];
    setTimeout(() => this.atualizarGrafico(), 1);
  }

  adicionarDividendo(): void {
    if (this.ativoSelecionadoParaDividendo?.id) {
      this.novoDividendo.valor = this.tratarMoeda(this.novoDividendo.valor);
      this.isLoadingAcao = true; // Trava o botão

      if (this.novoDividendo.id) {
        this.ativoService.atualizarDividendo(this.ativoSelecionadoParaDividendo.id, this.novoDividendo)
          .subscribe(divAtualizado => {
            const index = this.dividendos.findIndex(d => d.id === divAtualizado.id);
            if (index !== -1) this.dividendos[index] = divAtualizado;
            this.limparFormularioDividendo();
            this.carregarTodosDividendos(); 
            this.isLoadingAcao = false; // Libera o botão
          });
      } else {
        this.ativoService.salvarDividendo(this.ativoSelecionadoParaDividendo.id, this.novoDividendo)
          .subscribe(divSalvo => {
            this.dividendos.push(divSalvo); 
            this.limparFormularioDividendo();
            this.carregarTodosDividendos(); 
            this.isLoadingAcao = false; // Libera o botão
          });
      }
    }
  }

  prepararEdicaoDividendo(div: Dividendo): void {
    this.novoDividendo = { ...div }; 
    this.novoDividendo.valor = div.valor.toFixed(2).replace('.', ',') as any; 
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  removerDividendo(id: number | undefined): void {
    if (id) {
      this.isLoadingAcao = true; // Trava botões
      this.ativoService.excluirDividendo(id).subscribe(() => {
        this.dividendos = this.dividendos.filter(div => div.id !== id);
        this.carregarTodosDividendos(); 
        this.isLoadingAcao = false; // Libera botões
      });
    }
  }

  limparFormularioDividendo(): void {
    this.novoDividendo = { valor: 0, dataPagamento: '' };
  }

  atualizarGrafico(): void {
    const canvas = document.getElementById('graficoCarteira') as HTMLCanvasElement;
    if (!canvas) return;

    let totalFii = 0; let totalAcao = 0; let totalRendaFixa = 0;

    this.ativos.forEach(ativo => {
      const valorTotal = ativo.quantidadeCotas * ativo.precoMedio;
      if (ativo.tipo === 'FII') totalFii += valorTotal;
      if (ativo.tipo === 'ACAO') totalAcao += valorTotal;
      if (ativo.tipo === 'RENDA_FIXA') totalRendaFixa += valorTotal;
    });

    if (this.grafico) this.grafico.destroy();

    this.grafico = new Chart(canvas, {
      type: 'doughnut', 
      data: {
        labels: ['FIIs', 'Ações', 'Renda Fixa'],
        datasets: [{
          data: [totalFii, totalAcao, totalRendaFixa],
          backgroundColor: ['#EC7000', '#1E2A4F', '#28a745'],
          borderWidth: 0
        }]
      },
      options: { responsive: true, plugins: { legend: { position: 'bottom' } } }
    });
  }
}