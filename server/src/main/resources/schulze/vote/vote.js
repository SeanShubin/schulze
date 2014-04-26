define([
    'jquery',
    'underscore',
    '/schulze/util/url-parser.js',
    'text!/schulze/vote/vote-page.html',
    'text!/schulze/vote/single-ranking-template.html'],
    function ($, _, parseUrl, votePageText, singleRankingTemplateText) {
        'use strict';
        function createVotePagePrototype(options) {
            var voterName, electionName, contract;

            function extractRanking(element) {
                var rank, candidate;
                rank = parseInt($(element).find('.ranking-rank').val(), 10);
                candidate = $(element).find('.ranking-candidate').html();
                if (_.isNaN(rank)) {
                    return {candidate: _.unescape(candidate)};
                } else {
                    candidate = $(element).find('.ranking-candidate').html();
                    return {rank: rank, candidate: _.unescape(candidate)};
                }
            }

            function applyRankingsPressed() {
                var rankings = _.map(options.dom.find('.ranking'), extractRanking);
                options.serverApi.setVote({electionName: electionName, voterName: voterName, rankings: rankings});
            }

            function rankingChange() {
                options.dom.find('.apply-rankings').show();
            }

            function resetVotePressed() {
                var rankings = _.map(options.dom.find('.ranking'), function (e) {
                    return {
                        candidate: $(e).find('.ranking-candidate').html()
                    }
                });
                options.serverApi.setVote({electionName: electionName, voterName: voterName, rankings: rankings});
            }

            function setupEventHandling() {
                options.dom.find('.apply-rankings').on('click', applyRankingsPressed);
                options.dom.find('.reset-vote').on('click', resetVotePressed);
            }

            function selectAllText() {
                var $this = $(this);
                $this.select();
                $this.mouseup(function () {
                    $this.unbind("mouseup");
                    return false;
                });
            }

            function initialize() {
                electionName = parseUrl(options.url).queryValuesFirstOnly.election;
                voterName = parseUrl(options.url).queryValuesFirstOnly.voter;
                options.dom.find('.voter-name').text(voterName);
                options.dom.find('.election-name').text(electionName);
                setupEventHandling();
                options.serverApi.interestedInVote({electionName: electionName, voterName: voterName});
                options.serverApi.interestedInVoterExistence(voterName);
                options.serverApi.interestedInElectionExistence(electionName);
                options.dom.find('.election-page-link').attr('href', '../election/election.html?name=' + encodeURIComponent(electionName));
                options.dom.find('.election-page-link').text('Back to Election "' + electionName + '"');
                options.dom.find('.voter-page-link').attr('href', '../voter/voter.html?name=' + encodeURIComponent(voterName));
                options.dom.find('.voter-page-link').text('Back to Voter "' + voterName + '"');
            }

            function notifyRegardingVote(rankings) {
                var compiled = _.template(options.rankingTemplate);

                function insertRanking(ranking) {
                    var expanded = compiled({
                        candidate: _.escape(ranking.candidate),
                        description: _.escape(ranking.description),
                        rank: ranking.rank
                    });
                    options.dom.find('.rankings').append(expanded)
                }

                options.dom.find('.ranking-rank').unbind();
                options.dom.find('.rankings').empty();
                _.each(rankings, insertRanking)
                options.dom.find('.ranking-rank').on('change', rankingChange);
                options.dom.find('.ranking-rank').on('keydown', rankingChange);
                options.dom.find('.ranking-rank').on('keypress', rankingChange);
                options.dom.find('.ranking-rank').on('keyup', rankingChange);
                options.dom.find('.ranking-rank').on('focus', selectAllText);
                options.dom.find('.apply-rankings').hide();
            }

            function handleVoterExistence(exists) {
                if (exists) {
                    options.dom.find('.voter-does-not-exist-section').hide();
                } else {
                    options.dom.find('.voter-does-not-exist-section').show();
                    options.dom.find('.vote-exists-section').hide();
                }
            }

            function handleElectionExistence(exists) {
                if (exists) {
                    options.dom.find('.election-does-not-exist-section').hide();
                } else {
                    options.dom.find('.election-does-not-exist-section').show();
                    options.dom.find('.vote-exists-section').hide();
                }
            }

            contract = {
                dom: options.dom,
                notifyRegardingVote: notifyRegardingVote,
                notifyRegardingElectionExistence: handleElectionExistence,
                notifyRegardingVoterExistence: handleVoterExistence,
                initialize: initialize
            }
            return contract;
        }

        function createVotePage(options) {
            options.dom = $('<div>' + votePageText + '</div>');
            options.rankingTemplate = singleRankingTemplateText;
            return createVotePagePrototype(options);
        }

        return createVotePage;
    });
