define([
    'jquery',
    'underscore',
    '/schulze/util/url-parser.js',
    'text!/schulze/voter/voter-page.html',
    'text!/schulze/voter/single-election-template.html'],
    function ($, _, parseUrl, voterPageText, singleElectionTemplateText) {
        'use strict';
        function createVoterPagePrototype(options) {
            var voterName, contract;

            function notifyRegardingElections(elections) {
                var compiled = _.template(options.electionTemplate);

                function insertElection(election) {
                    var expanded = compiled({
                        electionName: _.escape(election.name),
                        urlEncodedElectionName: encodeURIComponent(election.name),
                        urlEncodedVoterName: encodeURIComponent(voterName)
                    });
                    options.dom.find('.elections').append(expanded)
                }

                options.dom.find('.elections').empty();
                _.each(elections, insertElection)
            }

            function hideVoter() {
                options.dom.find('.voter-exists').hide();
                options.dom.find('.voter-does-not-exist').hide();
                options.dom.find('.voter-scheduled-for-deletion').hide();
            }

            function showExistingVoter() {
                hideVoter();
                options.dom.find('.voter-exists').show();
                options.dom.find('.voter-name').text(voterName);
            }

            function showDeletedVoter() {
                hideVoter();
                options.dom.find('.voter-does-not-exist').show();
                options.dom.find('.voter-name').text(voterName);
            }

            function showVoterScheduledForDeletion() {
                hideVoter();
                options.dom.find('.voter-scheduled-for-deletion').show();
                options.dom.find('.voter-name').text(voterName);
            }

            function notifyRegardingVoterExistence(voterExists) {
                if (voterExists) {
                    showExistingVoter();
                } else {
                    showDeletedVoter();
                }
            }

            function setupEventHandling() {
                options.dom.find('.delete-voter').on('click', function () {
                    showVoterScheduledForDeletion();
                    options.serverApi.deleteVoter(voterName);
                });
            }

            function initialize() {
                voterName = parseUrl(options.url).queryValuesFirstOnly.name;
                setupEventHandling();
                options.serverApi.interestedInElections();
                options.serverApi.interestedInVoterExistence(voterName);
            }

            contract = {
                dom: options.dom,
                notifyRegardingElections: notifyRegardingElections,
                notifyRegardingVoterExistence: notifyRegardingVoterExistence,
                initialize: initialize
            }
            return contract;
        }

        function createVoterPage(options) {
            options.dom = $('<div>' + voterPageText + '</div>');
            options.electionTemplate = singleElectionTemplateText;
            return createVoterPagePrototype(options);
        }

        return createVoterPage;
    });
