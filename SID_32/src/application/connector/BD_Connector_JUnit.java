package application.connector;

import java.sql.SQLException;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.connector.objects.Cultura;
import application.connector.objects.Medicao;
import application.connector.objects.Variavel;
import application.connector.objects.VariavelMedida;

class BD_Connector_JUnit {

	BD_GUI_Connector b = new BD_GUI_Connector();

	@BeforeAll
	static void print() {
		System.out.println("CONNECTION ESTABLISHED");
		System.out.println("Junit Test started\n");
		System.out.println("---------------------");
	}

	@BeforeEach
	void testStarting() throws SQLException {
		System.out.println("STARTING ANOTHER TEST:\n\n");
		b.login("root", "1221");
	}

	@AfterEach
	void testFinished() throws SQLException {
		System.out.println("------------------");
		System.out.println("TEST FINISHED! \n ");
		b.logout();
	}

	@Test
	void insertCultura() {
		try {
			b.insertCultura("nome-_-Cultura", "descricao", 6 , 1);
		} catch (SQLException e) {
			System.out.println("No permissons to insert on that given table, or wrong fields");
		}
	}

	@Test
	void getCulturaTable() {
		try {
			LinkedList<Cultura> list = b.getCulturaTable();
			for (Cultura c : list) {
				System.out.println(
						"Cultura com o id: " + c.getId_cultura() + "\nNome: " + c.getNome_cultura() + "\n\nNEXT\n");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void MedicaoByCultura() {
		try {
			LinkedList<Medicao> list = b.getMedicoesCultura(3);
			for (Medicao m : list)
				System.out.println(
						"Medicao com o id: " + m.getId_medicao() + "\nValor: " + m.getValor_medicao() + "\n\nNEXT\n");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void getUsername() {
		System.out.println(b.getUsername());
	}

	@Test
	void getPassword() {
		System.out.println(b.getPassword());
	}

	@Test
	void getAllTablesNames() {
		try {
			LinkedList<String> list = b.getAllTables();
			for (String s : list)
				System.out.println("Table com o nome: " + s + "\n\nNEXT\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getTableVarivel() {
		try {
			LinkedList<Variavel> list = b.getVariavelTable();
			for (Variavel v : list)
				System.out.println("Variavel com o id: " + v.getId_variavel() + "\nVariavel com o nome: "
						+ v.getNome_variavel() + "\n\nNEXT\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getVariaveisCultura() {
		try {
			LinkedList<Variavel> list = b.getVariaveisCultura(3);
			for (Variavel v : list)
				System.out.println("Variavel com o id: " + v.getId_variavel() + "\nVariavel com o nome: "
						+ v.getNome_variavel() + "\n\nNEXT\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getVariavelMedidaTable() {
		try {
			LinkedList<VariavelMedida> list = b.getVariavelMedidaTable();
			for (VariavelMedida v : list)
				System.out.println("Variavel com o id: " + v.getVariavel_medida_id()
						+ "\nVariavel com a cultura com o id: " + v.getCultura_fk() + "\n\nNEXT\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getMedicaoTable() {
		try {
			LinkedList<Medicao> list = b.getMedicaoTable();
			for (Medicao m : list)
				System.out.println("Medicao com o id: " + m.getId_medicao() + "\nMedicao com o valor: "
						+ m.getValor_medicao() + "\n\nNEXT\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getMedicoesCulturaVariavel() {
		try {
			LinkedList<LinkedList<Medicao>> list = b.getMedicoesCulturaByVariavel(3);
			int i = 0;
			for (LinkedList<Medicao> v : list) {
				System.out.println(i);
				for (Medicao m : v)
					System.out.println("Medicao com o id: " + m.getId_medicao() + "\nMedicao com o valor: "
							+ m.getValor_medicao() + "\n\nNEXT\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getTableColumn() {
		try {
			LinkedList<String> list = b.getTableColumn("cultura", "Nome_Cultura");
			System.out.println("O conteudo dessa coluna é:");
			for (String s : list)
				System.out.println(s);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void insertMedicao() {
		try {
			b.insertMedicao(5 , 12);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void insertVariavel() {
		String s[] = { "ishdiau" };
		try {
			b.insertVariavel("Pretty");
		} catch (SQLException e) {
		}
	}

	@Test
	void insertVariavelMedida() {
		try {
			b.insertVariavelMedida(15, 20, 1);
		} catch (SQLException e) {
		}
	}

	@Test
	void deleteVariavel() throws NumberFormatException, SQLException {
		b.deleteVariavel(Integer.parseInt(b.getVariavelTable().getLast().getId_variavel()));
	}

	@Test
	void deleteCultura() throws NumberFormatException, SQLException {
		b.deleteCultura(Integer.parseInt(b.getCulturaTable().getLast().getId_cultura()));
	}

	@Test
	void deleteVariavelMedida() throws NumberFormatException, SQLException {
		b.deleteVariavelMedida(Integer.parseInt(b.getVariavelMedidaTable().getLast().getVariavel_medida_id()));
	}

	@Test
	void deleteMedicao() throws NumberFormatException, SQLException {
		b.deleteMedicao(Integer.parseInt(b.getMedicaoTable().getLast().getId_medicao()));
	}
	
	@Test 
	void insertInvestigador(){
		
		try {
			b.insertInvestigador("sabio", "idk", "categoria", "secret-very");
		} catch (SQLException e) {
			System.out.println("NO PERMISSONS");
		}
	}

}
