package ItaipuHotelManager.manager.frontend.client.management;

import ItaipuHotelManager.manager.entities.HotelClient;
import ItaipuHotelManager.manager.entities.utils.CpfValidation;
import org.apache.commons.text.WordUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientUi {
    private final JFrame frame;
    private final JPanel panel;
    private final JButton btnCarregar;
    private final JButton btnCadastrar;
    private final JTable clienteTable;
    private JTextField txtBuscarCpf;
    private JButton btnBuscar;
    private JButton btnHospedagens;

    public ClientUi() {
        frame = new JFrame("Clientes");
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        btnCarregar = new JButton("Carregar Clientes");
        btnCarregar.setBackground(new Color(42, 60, 72));
        btnCarregar.setForeground(Color.white);

        btnCadastrar = new JButton("Cadastrar Cliente");
        btnCadastrar.setBackground(new Color(42, 60, 72));
        btnCadastrar.setForeground(Color.white);

        btnHospedagens = new JButton("Ver Hospedagens");
        btnHospedagens.setBackground(new Color(42, 60, 72));
        btnHospedagens.setForeground(Color.white);

        txtBuscarCpf = new JTextField(15);
        btnBuscar = new JButton("Buscar por CPF");
        btnBuscar.setBackground(new Color(42, 60, 72));
        btnBuscar.setForeground(Color.white);

        String[] columnNames = {"ID", "Nome", "CPF", "Endereço"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        clienteTable = new JTable(model);

        buttonPanel.add(btnCarregar);
        buttonPanel.add(btnCadastrar);
        buttonPanel.add(btnHospedagens);

        btnCarregar.addActionListener(e -> carregarClientes(model));
        btnCadastrar.addActionListener(e -> abrirCadastro());
        btnHospedagens.addActionListener(e -> new HostingClientsUi());

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(clienteTable), BorderLayout.CENTER);

        JPanel buscarPanel = new JPanel();
        buscarPanel.add(new JLabel("CPF:"));
        buscarPanel.add(txtBuscarCpf);
        buscarPanel.add(btnBuscar);
        panel.add(buscarPanel, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> {
            String cpf = txtBuscarCpf.getText().trim();

            if (cpf.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Digite um CPF para buscar!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/clients/" + cpf;

            try {
                ResponseEntity<HotelClient> response = restTemplate.exchange(url, HttpMethod.GET, null, HotelClient.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    HotelClient cliente = response.getBody();

                    assert cliente != null;
                    JOptionPane.showMessageDialog(frame,
                            "Nome: " + cliente.getFullName() + "\n" +
                                    "CPF: " + cliente.getCpf() + "\n" +
                                    "Endereço: " + cliente.getAddress() + "\n" +
                                    "Cidade: " + cliente.getCity() + "\n" +
                                    "Telefone: " + cliente.getPhone() + "\n" +
                                    "Email: " + cliente.getEmail() + "\n" +
                                    "CNPJ: " + cliente.getCnpj(),
                            "Cliente Encontrado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Cliente não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao buscar cliente: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void carregarClientes(DefaultTableModel model) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/clients";

        try {
            ResponseEntity<List<HotelClient>> response = restTemplate.exchange(url,
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<HotelClient>>() {}
            );

            List<HotelClient> clientes = response.getBody();
            model.setRowCount(0);

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
        cadastroPanel.setLayout(new GridLayout(8, 2));

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
        btnSalvar.setBackground(new Color(42, 60, 72));
        btnSalvar.setForeground(Color.white);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(42, 60, 72));
        btnCancelar.setForeground(Color.white);

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

        btnSalvar.addActionListener(e -> {
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

            HotelClient novoCliente = new HotelClient(null, WordUtils.capitalizeFully(nome) ,
                    cpf, WordUtils.capitalizeFully(cidade),
                    WordUtils.capitalizeFully(endereco), email, telefone, cnpj);

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/clients";

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<HotelClient> request = new HttpEntity<>(novoCliente, headers);
                ResponseEntity<HotelClient> response = restTemplate.exchange(url, HttpMethod.POST, request, HotelClient.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    JOptionPane.showMessageDialog(cadastroDialog, "Cliente cadastrado com sucesso!");
                    cadastroDialog.dispose();
                    carregarClientes((DefaultTableModel) clienteTable.getModel());
                }
                else {
                    JOptionPane.showMessageDialog(cadastroDialog, "Erro inesperado ao cadastrar cliente. Status: " + response.getStatusCode(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HttpStatusCodeException ee){
                JOptionPane.showMessageDialog(cadastroDialog, "Cliente já cadastrado.");

            }
            catch (Exception eee) {
                JOptionPane.showMessageDialog(cadastroDialog, "Erro ao cadastrar cliente: " + eee.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> {
            cadastroDialog.dispose();
        });
        cadastroDialog.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientUi::new);
    }
}
