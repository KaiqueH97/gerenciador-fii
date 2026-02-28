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
  modoCadastro: boolean = false;
  isLoadingCadastro: boolean = false;
  mensagemCadastro: string = '';

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
    this.isLoadingLogin = true; 
    
    this.ativoService.testarLogin(this.usuarioLogin, this.senhaLogin).subscribe({
      next: () => {
        const token = btoa(this.usuarioLogin + ':' + this.senhaLogin);
        localStorage.setItem('meuTokenDeAcesso', token);
        this.isAutenticado = true;
        this.erroLogin = false;
        this.isLoadingLogin = false; 
        this.iniciarSistema();
      },
      error: (erro) => {
        this.isLoadingLogin = false; // Sempre libera o botão se der erro!
        
        // Se o erro for 500 (Banco dormindo) ou 0 (Render dormindo)
        if (erro.status === 500 || erro.status === 0 || erro.status === 503) {
          alert('O servidor e o banco de dados estão acordando do modo de economia de energia. Por favor, aguarde 15 segundos e clique em Entrar novamente!');
        } else {
          // Se for erro 401, a senha está errada mesmo
          this.erroLogin = true;
        }
      }
    });
  }

iniciarSistema(): void {
    const ativosCache = localStorage.getItem('ativosCache');
    if (ativosCache) {
      this.ativos = JSON.parse(ativosCache); // Transforma o texto de volta em lista
      this.calcularPatrimonio();
      setTimeout(() => this.atualizarGrafico(), 1);
    }

    const dividendosCache = localStorage.getItem('dividendosCache');
    if (dividendosCache) {
      const dadosDiv = JSON.parse(dividendosCache);
      this.totalDividendos = dadosDiv.reduce((soma: any, div: any) => soma + div.valor, 0);
    }

    this.carregarAtivos();
    this.carregarTodosDividendos();
  }

  carregarAtivos(): void {
    this.ativoService.listarAtivos().subscribe(dados => {
      this.ativos = dados;
      
      // Salva a versão mais nova e atualizada no cache do navegador
      localStorage.setItem('ativosCache', JSON.stringify(dados));
      
      this.calcularPatrimonio();
      setTimeout(() => this.atualizarGrafico(), 1); 
    });
  }

  carregarTodosDividendos(): void {
    this.ativoService.listarTodosDividendos().subscribe(dados => {
      
      // Salva a versão mais nova e atualizada no cache do navegador
      localStorage.setItem('dividendosCache', JSON.stringify(dados));
      
      this.totalDividendos = dados.reduce((soma, div) => soma + div.valor, 0);
    });
  }

  sair(): void {
    localStorage.removeItem('meuTokenDeAcesso');
    
    // Por segurança, apagamos o cache financeiro quando o usuário faz logout
    localStorage.removeItem('ativosCache');
    localStorage.removeItem('dividendosCache');
    
    this.isAutenticado = false;
    this.ativos = [];
    this.dividendos = [];
    this.usuarioLogin = '';
    this.senhaLogin = '';
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
      if (valor > 0) {
        return this.formatarMoedaBR(valor / this.novoAtivo.quantidadeCotas);
      }
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
      this.ativoService.atualizarAtivo(this.novoAtivo.id, this.novoAtivo).subscribe({
        next: (ativoAtualizado) => {
          const index = this.ativos.findIndex(a => a.id === ativoAtualizado.id);
          if (index !== -1) this.ativos[index] = ativoAtualizado;
          this.calcularPatrimonio();
          setTimeout(() => this.atualizarGrafico(), 1);
          this.limparFormulario();
          this.isLoadingAcao = false; // Libera o botão
        },
        error: (erro) => {
          console.error(erro);
          alert('Erro ao salvar. O servidor pode estar acordando, aguarde uns segundos e tente novamente.');
          this.isLoadingAcao = false; // LIBERA O BOTÃO MESMO COM ERRO!
        }
      });
    } else {
      this.ativoService.salvarAtivo(this.novoAtivo).subscribe({
        next: (ativoSalvo) => {
          this.ativos.push(ativoSalvo);
          this.calcularPatrimonio();
          setTimeout(() => this.atualizarGrafico(), 1);
          this.limparFormulario();
          this.isLoadingAcao = false; // Libera o botão
        },
        error: (erro) => {
          console.error(erro);
          alert('Erro ao salvar. O servidor pode estar acordando, aguarde uns segundos e tente novamente.');
          this.isLoadingAcao = false; // LIBERA O BOTÃO MESMO COM ERRO!
        }
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
        this.isLoadingAcao = true; 
        this.ativoService.excluirAtivo(id).subscribe({
          next: () => {
            this.ativos = this.ativos.filter(a => a.id !== id);
            this.calcularPatrimonio();
            this.carregarTodosDividendos(); 
            setTimeout(() => this.atualizarGrafico(), 1);
            this.isLoadingAcao = false;
          },
          error: (erro) => {
            alert('Erro ao excluir. Tente novamente.');
            this.isLoadingAcao = false; // LIBERA O BOTÃO
          }
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
      this.isLoadingAcao = true;

      if (this.novoDividendo.id) {
        this.ativoService.atualizarDividendo(this.ativoSelecionadoParaDividendo.id, this.novoDividendo).subscribe({
          next: (divAtualizado) => {
            const index = this.dividendos.findIndex(d => d.id === divAtualizado.id);
            if (index !== -1) this.dividendos[index] = divAtualizado;
            this.limparFormularioDividendo();
            this.carregarTodosDividendos(); 
            this.isLoadingAcao = false;
          },
          error: (erro) => {
            alert('Erro ao salvar dividendo. Tente novamente.');
            this.isLoadingAcao = false; // LIBERA O BOTÃO
          }
        });
      } else {
        this.ativoService.salvarDividendo(this.ativoSelecionadoParaDividendo.id, this.novoDividendo).subscribe({
          next: (divSalvo) => {
            this.dividendos.push(divSalvo); 
            this.limparFormularioDividendo();
            this.carregarTodosDividendos(); 
            this.isLoadingAcao = false;
          },
          error: (erro) => {
            alert('Erro ao salvar dividendo. Tente novamente.');
            this.isLoadingAcao = false; // LIBERA O BOTÃO
          }
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
      this.isLoadingAcao = true;
      this.ativoService.excluirDividendo(id).subscribe({
        next: () => {
          this.dividendos = this.dividendos.filter(div => div.id !== id);
          this.carregarTodosDividendos(); 
          this.isLoadingAcao = false;
        },
        error: (erro) => {
          alert('Erro ao excluir dividendo. Tente novamente.');
          this.isLoadingAcao = false; // LIBERA O BOTÃO
        }
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

  // padrão brasileiro
  formatarMoedaBR(valor: number): string {
    if (!valor) return '0,00';
    return valor.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  alternarModoLogin(): void {
    this.modoCadastro = !this.modoCadastro;
    this.erroLogin = false;
    this.mensagemCadastro = '';
    this.usuarioLogin = '';
    this.senhaLogin = '';
  }

  cadastrarUsuario(): void {
    if (!this.usuarioLogin || !this.senhaLogin) {
      this.mensagemCadastro = 'Preencha usuário e senha!';
      return;
    }

    this.isLoadingCadastro = true;
    this.mensagemCadastro = '';

    this.ativoService.registrarUsuario(this.usuarioLogin, this.senhaLogin).subscribe({
      next: (resposta) => {
        this.mensagemCadastro = '✅ Conta criada com sucesso! Você já pode fazer login.';
        this.isLoadingCadastro = false;
        // Limpa a senha, mas deixa o usuário preenchido para facilitar o login
        this.senhaLogin = ''; 
        
        // Volta para a tela de login depois de 2 segundos
        setTimeout(() => {
          this.modoCadastro = false;
          this.mensagemCadastro = '';
        }, 2500);
      },
      error: (erro) => {
        this.mensagemCadastro = '❌ Erro: Este usuário já existe ou o servidor falhou.';
        this.isLoadingCadastro = false;
      }
    });
  }
}