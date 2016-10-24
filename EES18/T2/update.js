$(document).ready(function() {
    update();
});

var update = function() {
    $('#loader').show();
    $.get('http://apimobile.footstats.net/temporeal/v1/Campeonato/ListarClassificacao?Token=0&IdCampeonato=475', {}, function(resp) {
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
        $('#loader').hide();
    });
};

var toTitleCase = function(str) {
    str = str.replace('_', ' ').replace('\.png', '');
    return str.replace(/(?:^|\s|-)\w/g, function(match) {
        return match.toUpperCase();
    });
};