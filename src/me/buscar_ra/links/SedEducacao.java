package me.buscar_ra.links;

public enum SedEducacao {

	AlterarSenha("https://sed.educacao.sp.gov.br/RecuperacaoSenha/AlterarLoginAluno"),
	Logar("https://sed.educacao.sp.gov.br/Logon/LogOn/"),
	MudarSenha("https://sed.educacao.sp.gov.br/Logon/AlterarSenha/");
	
	
	public String url;
	
	SedEducacao(String site) {
		url = site;
	}
	
}
