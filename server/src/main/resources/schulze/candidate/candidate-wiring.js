require([
    'jquery',
    '/schulze/util/json-over-http.js',
    '/schulze/candidate/candidate.js',
    'text!/schulze/candidate/candidate-page.html',
    '/schulze/util/url-parser.js'],
    function ($, jsonOverHttp, candidateLogic, candidateHtml, parseUrl) {
        'use strict';
        var body = $('body'),
            parsedUrl = parseUrl(location);
        body.html(candidateHtml);

        candidateLogic({
            view: body,
            sendRequest: jsonOverHttp,
            candidateName: parsedUrl.queryValuesFirstOnly.candidate,
            electionName: parsedUrl.queryValuesFirstOnly.election
        });
    });
