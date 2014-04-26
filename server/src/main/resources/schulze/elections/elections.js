define([
    'jquery',
    'underscore',
    'text!/schulze/elections/elections-page.html',
    'text!/schulze/elections/single-election-template.html'],
    function ($, _, electionsPageText, singleElectionTemplateText) {
        'use strict';
        function createElectionsPagePrototype(options) {
            var electionNameField, ENTER_KEY, contract;

            ENTER_KEY = 13;
            electionNameField = $(options.dom.find('.election-name'));
            function isBlank(str) {
                return (!str || /^\s*$/.test(str));
            }

            function notifyRegardingElections(elections) {
                var compiled = _.template(options.electionTemplate);

                function insertElection(election) {
                    var expanded = compiled({name: _.escape(election.name), urlEncodedName: encodeURIComponent(election.name)});
                    options.dom.find('.elections').append(expanded);
                }

                options.dom.find('.elections').empty();
                _.each(elections, insertElection);
            }

            function addElectionClicked() {
                if (!isBlank(electionNameField.val())) {
                    options.serverApi.addElection(electionNameField.val());
                    electionNameField.val('');
                }
            }

            function electionNameKeyPress(e) {
                if (e.keyCode === ENTER_KEY) {
                    addElectionClicked();
                }
            }

            function setupEventHandling() {
                options.dom.find('.add-election').on('click', addElectionClicked);
                options.dom.find('.election-name').on('keypress', electionNameKeyPress);
            }

            function initialize() {
                setupEventHandling();
                options.serverApi.interestedInElections();
            }

            contract = {
                dom: options.dom,
                notifyRegardingElections: notifyRegardingElections,
                initialize: initialize
            };
            return contract;
        }

        function createElectionsPage(options) {
            options.dom = $('<div>' + electionsPageText + '</div>');
            options.electionTemplate = singleElectionTemplateText;
            return createElectionsPagePrototype(options);
        }

        return createElectionsPage;
    });
