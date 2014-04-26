require([
    'jquery',
    '/schulze/util/json-over-http.js',
    '/schulze/tally/tally.js',
    'text!/schulze/tally/tally-page.html',
    '/schulze/util/url-parser.js'],
    function ($, jsonOverHttp, createTallyPage, tallyContent, parseUrl) {
        'use strict';
        var body = $('body');
        body.html(tallyContent);
        createTallyPage(
            {
                view: body,
                sendRequest: jsonOverHttp,
                electionName: parseUrl(location).queryValuesFirstOnly.electionName
            }
        );
    });
