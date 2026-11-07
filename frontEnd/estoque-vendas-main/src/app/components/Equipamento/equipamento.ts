import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Equipamento } from '../../models/equipamento'; 
import { EquipamentoService } from '../../services/auth-cadastro-equipamento'; 

@Component({
  selector: 'app-equipamento',
  standalone: true, 
  imports: [CommonModule, ReactiveFormsModule], 
  templateUrl: './equipamento.html',
  styleUrls: ['./equipamento.css']
})
export class EquipamentoComponent implements OnInit {
  equipamentos: Equipamento[] = [];
  equipamentoForm: FormGroup;
  editandoId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private equipamentoService: EquipamentoService
  ) {
    this.equipamentoForm = this.fb.group({
      modelo: ['', Validators.required],
      numeroSerie: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.carregarEquipamentos();
  }

  carregarEquipamentos(): void {
    this.equipamentoService.listar().subscribe({
      next: (dados) => this.equipamentos = dados,
      error: (err) => console.error('Erro ao carregar equipamentos', err)
    });
  }

  salvar(): void {
    if (this.equipamentoForm.invalid) return;

    const dadosForm = this.equipamentoForm.value;

    if (this.editandoId) {
      this.equipamentoService.atualizar(this.editandoId, dadosForm).subscribe({
        next: () => {
          this.carregarEquipamentos();
          this.cancelarEdicao();
        },
        error: (err) => console.error('Erro ao atualizar', err)
      });
    } else {
      this.equipamentoService.cadastrar(dadosForm).subscribe({
        next: () => {
          this.carregarEquipamentos();
          this.equipamentoForm.reset(); // Limpa o form após salvar
        },
        error: (err) => console.error('Erro ao cadastrar', err)
      });
    }
  }

  editar(equipamento: Equipamento): void {
    if (!equipamento.id) return;
    this.editandoId = equipamento.id;
    this.equipamentoForm.patchValue({
      modelo: equipamento.modelo,
      numeroSerie: equipamento.numeroSerie
    });
  }

  cancelarEdicao(): void {
    this.editandoId = null;
    this.equipamentoForm.reset(); // Limpa o form ao cancelar
  }

  deletar(id: number | undefined): void {
    if (!id) return;
    if (confirm('Tem certeza que deseja deletar este equipamento?')) {
      this.equipamentoService.deletar(id).subscribe({
        next: () => this.carregarEquipamentos(),
        error: (err) => console.error('Erro ao deletar', err)
      });
    }
  }
}