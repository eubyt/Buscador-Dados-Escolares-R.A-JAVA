package me.buscar_ra;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.buscar_ra.conectar.Conectar;
import me.buscar_ra.links.SedEducacao;
import me.buscar_ra.modulo.Aluno;

public class Buscar {
	
	public Aluno aluno;
	
	private String dados_usuario;
	private String dados_senha;
	private String token;
	private String nova_senha = "Felipe157";
	private List<HttpCookie> cookies;
	
	
	
	public Buscar(String RA, String Digito, String estado, String data_nascimento) {
		
		String dataEmUmFormato = data_nascimento;
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date data = null;
		try {
			data = formato.parse(dataEmUmFormato);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		formato.applyPattern("yyyy-MM-dd");
		String dataFormatada = formato.format(data);
		
		this.aluno = new Aluno();
		aluno.RA = RA;
		aluno.Digito = Digito;
		aluno.Estado = estado;
		aluno.Data_Nascimento = dataFormatada;	
		AlterarSenha();
		PegarToken();
		MudarSenha();
		Conectar();
		}
	
	
	
	private void AlterarSenha() {

		String post = "Numero=" + aluno.RA +"&Digito=" + aluno.Digito +"&UF=" + aluno.Estado +"&DataNascimento=" + aluno.Data_Nascimento;
		Conectar conectar = new Conectar(SedEducacao.AlterarSenha, post);
		
		String resposta = conectar.resposta();

		JSONObject json = new JSONObject(resposta);
		String dados[] = new String[2];
		dados = json.getString("Msg").split("  -  ");
		
		dados_senha = dados[1].replace("Sua senha é: ", "");
		dados_usuario = dados[0].replace("Seu login é: ", "");
		
	}
	
	private void PegarToken() {
		
		
		String post = "usuario=" + dados_usuario +"&senha=" + dados_senha;
		Conectar conectar = new Conectar(SedEducacao.Logar, post);
		
		String resposta = conectar.resposta();

		JSONObject json = new JSONObject(resposta);
		token = json.getString("Token");
			
	}
	
	private void MudarSenha() {
		
		String post = "token=" + token +"&senhaAtual=" + dados_senha + "&senhaNova=" + nova_senha;
		Conectar conectar = new Conectar(SedEducacao.MudarSenha, post);
		conectar.resposta();
		
	}
	
	
	private void Conectar() {
		
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
		
		String post = "usuario=" + dados_usuario +"&senha=" + nova_senha;
		Conectar conectar = new Conectar(SedEducacao.Logar, post);
		
		String resposta = conectar.resposta();
		
	    cookies = cookieManager.getCookieStore().getCookies();

	    try {
			PaginaHTML();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private HttpURLConnection con;
	
	private void PaginaHTML() throws Exception {

	    String url = "https://sed.educacao.sp.gov.br/Aluno/ConsultaAluno";

	    URL obj = new URL(url);
	    con = (HttpURLConnection) obj.openConnection();
	    
	    for (HttpCookie cookie : cookies) {
            con.setRequestProperty("Cookie", cookie.getName() + "=" + cookie.getValue());
        }

	    con.setRequestMethod("GET");
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");

	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();

	    
	    org.jsoup.nodes.Document doc = Jsoup.parse(response.toString());
	   
	    Capturar(doc);
	    
	    Element table = doc.select("table").get(0); 
	    Elements rows = table.select("tr");

	    for (int i = 1; i < rows.size(); i++) {
	        Element row = rows.get(i);
	        Elements cols = row.select("td");
	        String telefone = "(" + cols.get(1).text() + ") " + cols.get(2).text();
	        aluno.telefones.add(telefone);
	    }
	}
	
	
	
	private void Capturar(org.jsoup.nodes.Document doc) {
		
	List<String> lista = Arrays.asList("NomeAluno", "NomePai", "NomeMae", "EstadoCivil", "Sexo", "CpfAluno", "RgAluno", "CidadeNascimento", "PaisNascimento", "DtNascimento", "nrRA", "nrDigRa", "sgUfRa", "Endereco", "EnderecoNR", "EnderecoComplemento", "EnderecoBairro", "EnderecoCEP", "EnderecoCidade");
	
	HashMap<String, String> dados = new HashMap<String, String>();
	
	for (String l : lista) {
	Elements listo = doc.select("input[id=" +l+ "]");
	String valor = listo.val();
	if ((valor == "") || (valor == null))
		valor = "N�o informado";
	
	dados.put(l, valor);
	}
	
	aluno.dados = dados;
	
	}
	
	

	 
	
	

}
