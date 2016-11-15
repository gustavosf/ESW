$(document).ready(function() {
    update();
});

var update = function() {
	var counter = 2;

    $('#loader').show();
    $.get('http://apimobile.footstats.net/temporeal/v1/Campeonato/ListarClassificacao?Token=0&IdCampeonato=475', {}, function(resp) {
    	$('#team-info').empty();
    	$('#points').empty();
        for (i in resp) {
            var tr = $('<tr>')
                .append($('<td>').text(resp[i].posicao))
                .append($('<td class="time">').html(toTitleCase(resp[i].urlLogo)))
                .appendTo('#team-info');
            var tr = $('<tr>')
                .append($('<td>').text(resp[i].jogos))
                .append($('<td>').text(resp[i].pontos))
                .append($('<td>').text(resp[i].vitorias))
                .append($('<td>').text(resp[i].empates))
                .append($('<td>').text(resp[i].derrotas))
                .append($('<td>').text(resp[i].golsPro))
                .append($('<td>').text(resp[i].golsSofridos))
                .append($('<td>').text(resp[i].saldoGols))
                .append($('<td>').text(Math.floor(resp[i].pontos * 100 / (resp[i].jogos * 3))+'%'))
                .appendTo('#points')
        }
        if (--counter == 0) $('#loader').hide();
    });

	$.get('http://apimobile.footstats.net/temporeal/v1/Campeonato/ListarCampeonatos?Token=0&Versao=din', {}, function(camps) {
	    $.get('http://apimobile.footstats.net/temporeal/v1/Partida/ListarPartidasCampeonato?Token=0',
	    	{'Rodada': camps[0].rodadaAtual, 'IdCampeonato': 475}, function(resp) {
	    	$('#num-rodada span').text(camps[0].rodadaAtual);
	    	$('#jogos').empty();
	        for (i in resp) {
	            var tr = $('<tr>')
	                .append($('<td>').html('<img src="http://footstats.net/clubIcons/'+resp[i].escudoMandante+'"/>'))
	                .append($('<td>').html(resp[i].siglaMandante))
	                .append($('<td>').html(resp[i].placarMandante != null ? resp[i].placarMandante : '-'))
	                .append($('<td>').html('x'))
	                .append($('<td>').html(resp[i].placarVisitante != null ?resp[i].placarVisitante : '-'))
	                .append($('<td>').html(resp[i].siglaVisitante))
	                .append($('<td>').html('<img src="http://footstats.net/clubIcons/'+resp[i].escudoVisitante+'"/>'))
	                .appendTo('#jogos');
	        }
	        if (--counter == 0) $('#loader').hide();
	    });
	});
};

var toTitleCase = function(str) {
    str = str.replace('_', ' ').replace('\.png', '');
    return str.replace(/(?:^|\s|-)\w/g, function(match) {
        return match.toUpperCase();
    });
};

var mostrarRodada = function() {
	$('#classificacao').hide();
	$('#rodada').show();
}

var mostrarClassificacao = function() {
	$('#classificacao').show();
	$('#rodada').hide();
}