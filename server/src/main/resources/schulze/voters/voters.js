define([
    'jquery',
    'underscore',
    'text!/schulze/voters/voters-page.html',
    'text!/schulze/voters/single-voter-template.html'],
    function ($, _, votersPageText, singleVoterTemplateText) {
        'use strict';
        function createVotersPagePrototype(options) {
            var voterNameField, ENTER_KEY, contract;

            ENTER_KEY = 13;
            voterNameField = $(options.dom.find('.voter-name'));
            function isBlank(str) {
                return (!str || /^\s*$/.test(str));
            }

            function notifyRegardingVoters(voters) {
                var compiled = _.template(options.voterTemplate);

                function insertVoterNamed(voter) {
                    var expanded = compiled({name: _.escape(voter.name), urlEncodedName: encodeURIComponent(voter.name)});
                    options.dom.find('.voters').append(expanded);
                }
                options.dom.find('.voters').empty();
                _.each(voters, insertVoterNamed);
            }

            function addVoterClicked() {
                if (!isBlank(voterNameField.val())) {
                    options.serverApi.addVoter(voterNameField.val());
                    voterNameField.val('');
                }
            }

            function voterNameKeypress(e) {
                if (e.keyCode === ENTER_KEY) {
                    addVoterClicked();
                }
            }

            function setupEventHandling() {
                options.dom.find('.add-voter').on('click', addVoterClicked);
                options.dom.find('.voter-name').on('keypress', voterNameKeypress);
            }

            function initialize() {
                setupEventHandling();
                options.serverApi.interestedInVoters();
            }

            contract = {
                dom: options.dom,
                notifyRegardingVoters: notifyRegardingVoters,
                initialize: initialize
            };
            return contract;
        }

        function createVotersPage(options) {
            options.dom = $('<div>' + votersPageText + '</div>');
            options.voterTemplate = singleVoterTemplateText;
            return createVotersPagePrototype(options);
        }

        return createVotersPage;
    });
