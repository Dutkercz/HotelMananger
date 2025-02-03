package ItaipuHotelManager.manager.frontend;

import ItaipuHotelManager.manager.entities.HotelClient;
import ItaipuHotelManager.manager.entities.utils.CpfValidation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ClientUi {
    private final JFrame frame;
    private final JPanel panel;
    private final JButton btnCarregar;
    private final JButton btnCadastrar;
    private final JTable clienteTable;

    public ClientUi() {
        frame = new JFrame("Clientes");
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        btnCarregar = new JButton("Carregar Clientes");
        btnCadastrar = new JButton("Cadastrar Cliente");

        // Criando a tabela
        String[] columnNames = {"ID", "Nome", "CPF", "Endereço"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        clienteTable = new JTable(model);

        // Adicionando os botões ao painel
        buttonPanel.add(btnCarregar);
        buttonPanel.add(btnCadastrar);

        // Ações dos botões
        btnCarregar.addActionListener(e -> carregarClientes(model));
        btnCadastrar.addActionListener(e -> abrirCadastro());

        // Adicionando o painel de botões e a tabela
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(clienteTable), BorderLayout.CENTER);

        // Configurações da janela
        frame.add(panel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void carregarClientes(DefaultTableModel model) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/clients";

        try {
            ResponseEntity<java.util.List<HotelClient>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<java.util.List<HotelClient>>() {}
            );

            List<HotelClient> clientes = response.getBody();
            model.setRowCount(0); // Limpa a tabela antes de preencher

            if (clientes != null) {
                for (HotelClient cliente : clientes) {
                    Object[] row = {cliente.getId(), cliente.getFullName(), cliente.getCpf(), cliente.getAddress()};
                    model.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Erro ao buscar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirCadastro() {
        JDialog cadastroDialog = new JDialog(frame, "Cadastro de Cliente", true);
        JPanel cadastroPanel = new JPanel();
        cadastroPanel.setLayout(new GridLayout(8, 2)); // Ajustado para caber todos os campos

        JLabel lblFullName = new JLabel("Nome Completo:");
        JTextField txtFullName = new JTextField(20);

        JLabel lblCpf = new JLabel("CPF:");
        JTextField txtCpf = new JTextField(20);

        JLabel lblAddress = new JLabel("Endereço:");
        JTextField txtAddress = new JTextField(20);

        JLabel lblCity = new JLabel("Cidade:");
        JTextField txtCity = new JTextField(20);

        JLabel lblPhone = new JLabel("Telefone:");
        JTextField txtPhone = new JTextField(20);

        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField(20);

        JLabel lblCnpj = new JLabel("CNPJ:");
        JTextField txtCnpj = new JTextField(20);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        cadastroPanel.add(lblFullName);
        cadastroPanel.add(txtFullName);
        cadastroPanel.add(lblCpf);
        cadastroPanel.add(txtCpf);
        cadastroPanel.add(lblAddress);
        cadastroPanel.add(txtAddress);
        cadastroPanel.add(lblCity);
        cadastroPanel.add(txtCity);
        cadastroPanel.add(lblPhone);
        cadastroPanel.add(txtPhone);
        cadastroPanel.add(lblEmail);
        cadastroPanel.add(txtEmail);
        cadastroPanel.add(lblCnpj);
        cadastroPanel.add(txtCnpj);
        cadastroPanel.add(btnSalvar);
        cadastroPanel.add(btnCancelar);

        cadastroDialog.add(cadastroPanel);
        cadastroDialog.pack();
        cadastroDialog.setLocationRelativeTo(frame);

        // Adicionando eventos aos botões ANTES de exibir o diálogo
        btnSalvar.addActionListener(e -> {
            JOptionPane.showMessageDialog(cadastroDialog, "Botão Salvar clicado!");

            String nome = txtFullName.getText();
            String cpf = txtCpf.getText();
            String endereco = txtAddress.getText();
            String cidade = txtCity.getText();
            String telefone = txtPhone.getText();
            String email = txtEmail.getText();
            String cnpj = txtCnpj.getText();

            if (nome.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
                JOptionPane.showMessageDialog(cadastroDialog, "Os campos CPF, Nome e Endereço são obrigatórios",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!CpfValidation.isValidCPF(cpf)) {
                JOptionPane.showMessageDialog(cadastroDialog, "CPF inválido. Por favor, digite um CPF válido.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            HotelClient novoCliente = new HotelClient(null, nome, cpf, cidade, endereco, email, telefone, cnpj);

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/clients";

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<HotelClient> request = new HttpEntity<>(novoCliente, headers);
                ResponseEntity<HotelClient> response = restTemplate.exchange(url, HttpMethod.POST, request, HotelClient.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    JOptionPane.showMessageDialog(cadastroDialog, "Cliente cadastrado com sucesso! Status: " + response.getStatusCode());
                    cadastroDialog.dispose();
                    carregarClientes((DefaultTableModel) clienteTable.getModel());
                } else {
                    JOptionPane.showMessageDialog(cadastroDialog, "Erro inesperado ao cadastrar cliente. Status: " + response.getStatusCode(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(cadastroDialog, "Erro ao cadastrar cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> {
            JOptionPane.showMessageDialog(cadastroDialog, "Botão Cancelar clicado!");
            cadastroDialog.dispose();
        });

        cadastroDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientUi::new);
    }
}