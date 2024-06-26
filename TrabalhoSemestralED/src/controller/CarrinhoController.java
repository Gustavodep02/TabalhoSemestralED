package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.Fila;
import model.Pilha;

public class CarrinhoController implements ActionListener {

	private JComboBox cliente;
	private JComboBox produto;
	private JTextField quantidade;
	private JTextArea taCarrinho;
	private JTextArea taCheckout;
	private JTextArea taVendas;
	private int contadorAdicionado =0;
	Pilha pilha = new Pilha();
	Fila<String> fila = new Fila();
	Fila<String> p1 = new Fila();

	public CarrinhoController(JComboBox cliente, JComboBox produto, JTextField quantidade, JTextArea taCarrinho,
			JTextArea taCheckout, JTextArea taVendas) {
		this.cliente = cliente;
		this.produto = produto;
		this.quantidade = quantidade;
		this.taCarrinho = taCarrinho;
		this.taCheckout = taCheckout;
		this.taVendas = taVendas;
		try {
			gerarProduto();
			gerarCliente();
			gerarVendas();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void adicionarCarrinho() throws Exception {

		String[] tipo = produto.getSelectedItem().toString().split(" | ");
		String produto = tipo[0];
		String quantidade = this.quantidade.getText();
		if (Integer.parseInt(quantidade) <= 0) {
			JOptionPane.showMessageDialog(null, "Erro: Quantidade inválida!");
		}
		alteraQuantidade(produto, quantidade, false);
		p1.insert(produto);
		String carrinho = "Produto:" + produto + " | " + "Quantidade:" + quantidade + ":\n";
		pilha.push(carrinho);
		taCarrinho.append(pilha.top());
		contadorAdicionado++;
	}

	public void gerarVendas() throws IOException {
		File arq = new File("./Venda.csv");
		taVendas.setText("");
		FileInputStream fluxo = new FileInputStream(arq);
		InputStreamReader leitor = new InputStreamReader(fluxo);
		BufferedReader buffer = new BufferedReader(leitor);
		String linha = buffer.readLine();
		linha = buffer.readLine();
		while (linha != null) {
			String valor = linha.replaceAll(";", " | ");
			taVendas.append(valor + "\n");
			linha = buffer.readLine();
		}
		buffer.close();
		leitor.close();
		fluxo.close();
	}

	public void gerarCliente() throws IOException {
		File arq = new File("./Cliente.csv");
		FileInputStream fluxo = new FileInputStream(arq);
		InputStreamReader leitor = new InputStreamReader(fluxo);
		BufferedReader buffer = new BufferedReader(leitor);
		String linha = buffer.readLine();
		linha = buffer.readLine();

		while (linha != null) {
			String valor = linha.replaceAll(";", " | ");
			cliente.addItem(valor);
			linha = buffer.readLine();
		}

		File arqJ = new File("./Cliente_juridico.csv");
		FileInputStream fluxoJ = new FileInputStream(arqJ);
		InputStreamReader leitorJ = new InputStreamReader(fluxoJ);
		BufferedReader bufferJ = new BufferedReader(leitorJ);
		String linhaJ = bufferJ.readLine();
		linhaJ = bufferJ.readLine();

		while (linhaJ != null) {
			String valorJ = linhaJ.replaceAll(";", " | ");
			cliente.addItem(valorJ);
			linhaJ = bufferJ.readLine();
		}
		buffer.close();
		leitor.close();
		fluxo.close();
		bufferJ.close();
		leitorJ.close();
		fluxoJ.close();
	}

	public void gerarProduto() throws IOException {
		File arq = new File("./Produto.csv");
		FileInputStream fluxo = new FileInputStream(arq);
		InputStreamReader leitor = new InputStreamReader(fluxo);
		BufferedReader buffer = new BufferedReader(leitor);
		String linha = buffer.readLine();
		linha = buffer.readLine();
		while (linha != null) {
			String valor = linha.replaceAll(";", " | ");
			produto.addItem(valor);
			linha = buffer.readLine();
		}
		buffer.close();
		leitor.close();
		fluxo.close();
	}

	public void removerCarrinho() throws Exception {
		if (contadorAdicionado == 0) {
            JOptionPane.showMessageDialog(null, "Erro: Carrinho vazio!");
        }else {
		String[] tipo = produto.getSelectedItem().toString().split(" | ");
		String produto = tipo[0];
		String quantidade = this.quantidade.getText().toString();
		alteraQuantidade(produto, quantidade, true);
		String pop = pilha.pop();
		String texto = taCarrinho.getText();
		texto = texto.replace(pop, "");
		taCarrinho.setText(texto);
		contadorAdicionado--;
        }
	}

	public void alteraQuantidade(String produto, String quantidade, boolean operacao) throws Exception {
		File arq = new File("./Produto.csv");

		if (!arq.exists() || !arq.isFile()) {
			throw new Exception("Arquivo inexistente");
		}

		FileInputStream fluxo = new FileInputStream(arq);
		InputStreamReader leitor = new InputStreamReader(fluxo);
		BufferedReader buffer = new BufferedReader(leitor);
		StringBuilder novoConteudo = new StringBuilder();
		String linha = buffer.readLine();
		if (linha != null) {
			novoConteudo.append(linha).append("\n");
			linha = buffer.readLine();
		}
		boolean quantidadeAlterada = false;

		while (linha != null) {
			String[] dados = linha.split(";");
			if (!dados[0].equals(produto)) {
				novoConteudo.append(linha).append("\r\n");
			} else {
				int total;
				if (operacao) {
					total = Integer.parseInt(dados[4]) + Integer.parseInt(quantidade);
				} else {
					total = Integer.parseInt(dados[4]) - Integer.parseInt(quantidade);
				}
				if (total < 0) {
					throw new Exception("Erro: Quantidade insuficiente!");
				}
				novoConteudo.append(dados[0]).append(";").append(dados[1]).append(";").append(dados[2]).append(";")
						.append(dados[3])
						.append(";").append(total).append(";").append(dados[5]).append("\r\n");
				quantidadeAlterada = true;
			}
			linha = buffer.readLine();
		}

		buffer.close();
		leitor.close();
		fluxo.close();

		if (quantidadeAlterada) {
			FileWriter fw = new FileWriter(arq);
			PrintWriter pw = new PrintWriter(fw);
			pw.write(novoConteudo.toString());
			pw.flush();
			pw.close();
			fw.close();
		}
		this.produto.removeAllItems();
		gerarProduto();
	}

	public void Checkout() throws Exception {
		if (contadorAdicionado == 0) {
            JOptionPane.showMessageDialog(null, "Erro: Carrinho vazio!");
        }else {
		File arq = new File("./Carrinho.csv");
		FileWriter fw = new FileWriter(arq);
		PrintWriter pw = new PrintWriter(fw);
		pw.write("\r\n" + taCarrinho.getText());
		pw.flush();
		pw.close();
		fw.close();
		taCheckout.setText("Compra Realizada com Sucesso!\n");
		Double valorItem = 0.0;
		int quantidade = 0;
		Double valor = 0.0;
		Double total = 0.0;
		while (!pilha.isEmpty()) {
			String[] splitString = pilha.top().split(":");
			quantidade = Integer.parseInt(splitString[2]);
			valor = pegaValor(p1.remove());
			valorItem = quantidade * valor;
			total += valorItem;
			String pop = pilha.pop();
			fila.insert(pop);
			String remove = fila.remove();
			taCheckout.append(remove + "Total do Item: " + valorItem + "\n");
		}
		taCheckout.append("Cliente: " + cliente.getSelectedItem().toString() + "\nTotal da Compra: " + total + "\n");
		cadastraVenda(cliente.getSelectedItem().toString(), total);
		taCarrinho.setText("");
		contadorAdicionado = 0;
        }
	}

	public Double pegaValor(String produto) throws IOException {
		File arq = new File("./Produto.csv"); 
		FileInputStream fluxo = new FileInputStream(arq);
		InputStreamReader leitor = new InputStreamReader(fluxo);
		BufferedReader buffer = new BufferedReader(leitor);
		String linha = buffer.readLine();
		linha = buffer.readLine();
		while (linha != null) {
			String dados[] = linha.split(";");
			if (dados[0].equals(produto)) {
				return Double.parseDouble(dados[2]);
			}
			linha = buffer.readLine();
		}
		buffer.close();
		leitor.close();
		fluxo.close();
		return null;
	}

	public void cadastraVenda(String cliente, Double total) throws IOException {
		File arq = new File("./Venda.csv");
		FileWriter fw = new FileWriter(arq, true);
		PrintWriter pw = new PrintWriter(fw);
		cliente = cliente.split(" | ")[0];
		pw.write("\r\n" + cliente + ";" + total);
		pw.flush();
		pw.close();
		fw.close();
		gerarVendas();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("Adicionar".equals(cmd)) {
			try {
				adicionarCarrinho();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if ("Remover".equals(cmd)) {
			try {
				removerCarrinho();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if ("Checkout".equals(cmd)) {
			try {
				Checkout();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
}
