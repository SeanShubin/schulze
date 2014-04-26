define([
    'jquery',
    'underscore',
    '/schulze/util/url-parser.js',
    'text!/schulze/election/election-page.html',
    'text!/schulze/election/single-candidate-template.html',
    'text!/schulze/election/single-voter-template.html'],
    function ($, _, parseUrl, electionPageText, singleCandidateTemplateText, singleVoterTemplateText) {
        'use strict';
        function createElectionPagePrototype(options) {
            var electionName, contract, ENTER_KEY, candidateNameField;

            ENTER_KEY = 13;
            candidateNameField = $(options.dom.find('.candidate-name'));
            function isBlank(str) {
                return (!str || /^\s*$/.test(str));
            }

            function notifyRegardingCandidates(candidates) {
                var compiled = _.template(options.candidateTemplate);

                function insertCandidate(candidate) {
                    var expanded = compiled({
                        name: _.escape(candidate.name),
                        description: _.escape(candidate.description),
                        urlEncodedElectionName: encodeURIComponent(electionName),
                        urlEncodedCandidateName: encodeURIComponent(candidate.name)
                    });
                    options.dom.find('.candidates').append(expanded);
                }

                options.dom.find('.candidates').empty();
                _.each(candidates, insertCandidate);
            }

            function notifyRegardingVoters(voters) {
                var compiled = _.template(options.voterTemplate);

                function insertVoter(voter) {
                    var expanded = compiled({
                        voterName: _.escape(voter.name),
                        urlEncodedVoterName: encodeURIComponent(voter.name),
                        urlEncodedElectionName: encodeURIComponent(electionName)
                    });
                    options.dom.find('.voters').append(expanded)
                }

                options.dom.find('.voters').empty();
                _.each(voters, insertVoter);
            }

            function addCandidateClicked() {
                if (!isBlank(candidateNameField.val())) {
                    options.serverApi.addCandidate({
                        electionName: electionName,
                        candidateName: candidateNameField.val()
                    });
                    candidateNameField.val('');
                    options.serverApi.interestedInCandidates(electionName);
                }
            }

            function candidateNameKeypress(e) {
                if (e.keyCode == ENTER_KEY) {
                    addCandidateClicked();
                }
            }

            function hideElection() {
                options.dom.find('.election-exists').hide();
                options.dom.find('.election-does-not-exist').hide();
                options.dom.find('.election-scheduled-for-deletion').hide();
                options.dom.find('.candidates-section').hide();
                options.dom.find('.voters-section').hide();
            }

            function showExistingElection() {
                hideElection();
                options.dom.find('.election-exists').show();
                options.dom.find('.election-name').text(electionName);
                options.dom.find('.candidates-section').show();
                options.dom.find('.voters-section').show();
            }

            function showDeletedElection() {
                hideElection();
                options.dom.find('.election-does-not-exist').show();
                options.dom.find('.election-name').text(electionName);
            }

            function showElectionScheduledForDeletion() {
                hideElection();
                options.dom.find('.election-scheduled-for-deletion').show();
                options.dom.find('.election-name').text(electionName);
                options.dom.find('.candidates-section').show();
                options.dom.find('.voters-section').show();
            }

            function notifyRegardingElectionExistence(electionExists) {
                if (electionExists) {
                    showExistingElection();
                } else {
                    showDeletedElection();
                }
            }

            function setupEventHandling() {
                options.dom.find('.delete-election').on('click', function () {
                    showElectionScheduledForDeletion();
                    options.serverApi.deleteElection(electionName);
                });
                options.dom.find('.add-candidate').on('click', addCandidateClicked);
                options.dom.find('.candidate-name').on('keypress', candidateNameKeypress);
            }

            function initialize() {
                electionName = decodeURIComponent(parseUrl(options.url).queryValuesFirstOnly.name);
                setupEventHandling();
                options.serverApi.interestedInCandidates(electionName);
                options.serverApi.interestedInVoters();
                options.serverApi.interestedInElectionExistence(electionName);
                options.dom.find('.tally').attr('href', '/schulze/tally/tally.html?electionName=' + encodeURIComponent(electionName));

            }

            contract = {
                dom: options.dom,
                notifyRegardingVoters: notifyRegardingVoters,
                notifyRegardingCandidates: notifyRegardingCandidates,
                notifyRegardingElectionExistence: notifyRegardingElectionExistence,
                initialize: initialize
            };
            return contract;
        }

        function createElectionPage(options) {
            options.dom = $('<div>' + electionPageText + '</div>');
            options.candidateTemplate = singleCandidateTemplateText;
            options.voterTemplate = singleVoterTemplateText;
            return createElectionPagePrototype(options);
        }

        return createElectionPage;
    });
