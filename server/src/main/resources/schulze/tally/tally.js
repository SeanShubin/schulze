define([], function () {
        "use strict";
        function tallyPage(options) {
            var view = options.view,
                sendRequest = options.sendRequest,
                electionName = options.electionName,
                tallyDetailsButton = view.find('.tally-details'),
                candidateNamesElement = view.find('.candidate-names'),
                placesElement = view.find('.places'),
                votesElement = view.find('.votes'),
                preferencesElement = view.find('.preferences'),
                floydWarshallElement = view.find('.floyd-warshall'),
                preferenceMatrixHeaderElement = view.find('.preference-matrix-header'),
                preferenceMatrixBodyElement = view.find('.preference-matrix-body'),
                strongestPathsElement = view.find('.strongest-paths'),
                strongestPathsHeaderElement = view.find('.strongest-paths-header'),
                strongestPathsBodyElement = view.find('.strongest-paths-body');

            function indexToPlaceText(index) {
                if (index === 0) return '1st';
                else if (index === 1) return '2nd';
                else if (index === 2) return '3rd';
                else return '' + (index + 1) + 'th';
            }

            function isSuccess(status) {
                return status >= 200 && status < 400;
            }

            function showPlaces(places) {
                view.find('.places-section').show();
                function composePlaceRow(tiedCandidates, index) {
                    function composePlaceCell(candidate) {
                        return '<td>' + _.escape(candidate) + '</td>';
                    }

                    return '<tr class="place"><th>' + indexToPlaceText(index) + '</th>' + _.map(tiedCandidates, composePlaceCell).join('') + '</tr>';
                }

                placesElement.html(_.map(places, composePlaceRow).join(''))
            }

            function getPlaces() {
                sendRequest({
                        method: 'GET',
                        uri: '/places/' + encodeURIComponent(electionName)},
                    function (response) {
                        if (isSuccess(response.status)) {
                            showPlaces(response.body)
                        }
                    });
            }

            function populateCandidateNames(candidateNames) {
                var composed;

                function composeCandidateNames(candidateNames) {
                    var result;

                    function composeCandidateName(candidateName) {
                        var result;
                        result = '<th>' + _.escape(candidateName) + '</th>';
                        return result;
                    }

                    result = '<th></th>' + _.map(candidateNames, composeCandidateName).join('');
                    return result;
                }

                composed = composeCandidateNames(candidateNames)
                candidateNamesElement.html(composed);
            }

            function composeVoterLink(voterName) {
                return '<a href="/schulze/vote/vote.html?election=' +
                    encodeURI(electionName) +
                    '&voter=' +
                    encodeURI(voterName) +
                    '">' + _.escape(voterName) +
                    '</a>';
            }

            function populateVotes(tally) {
                function composeVoteRow(voterName) {
                    function composeVoteCell(candidateName) {
                        return '<td>' + tally.votes[voterName][candidateName] + '</td>'
                    }

                    return '<tr><th>' + composeVoterLink(voterName) + '</th>' + _.map(tally.candidateNames, composeVoteCell).join('') + '</tr>'
                }

                votesElement.html(_.map(tally.voterNames, composeVoteRow).join(''));
            }

            function populatePreferences(tally) {
                function composeWinnerRows(winner) {
                    function composeLoserRow(loser) {
                        if (winner === loser) {
                            return '';
                        } else {
                            var result;
                            result = '<tr><td>' + _.escape(winner) + '</td>' +
                                '<td>' + tally.preferences[winner][loser] + '</td>' +
                                '<td>' + _.escape(loser) + '</td></tr>';
                            return result;
                        }
                    }

                    return _.map(tally.candidateNames, composeLoserRow).join('');
                }

                preferencesElement.html(_.map(tally.candidateNames, composeWinnerRows).join(''));
            }

            function populatePreferenceMatrix(tally) {
                function composeHeaderCell(candidateName) {
                    return '<th>' + _.escape(candidateName) + '</th>';
                }

                function composeRow(winner) {
                    function composeCell(loser) {
                        if (winner === loser) return '<td></td>';
                        else return '<td>' + tally.preferences[winner][loser] + '</td>';
                    }

                    return '<tr><th>' + _.escape(winner) + '</th>' + _.map(tally.candidateNames, composeCell).join('') + '</tr>';
                }

                preferenceMatrixHeaderElement.html('<th></th>' + _.map(tally.candidateNames, composeHeaderCell).join(''));
                preferenceMatrixBodyElement.html(_.map(tally.candidateNames, composeRow).join(''))
            }

            function hideAll() {
                view.find('.election-exists-section').hide();
                view.find('.election-does-not-exist-section').hide();
                view.find('.problem-with-server-section').hide();
                view.find('.details-section').hide();
            }

            function getTally() {
                sendRequest({
                        method: 'GET',
                        uri: '/tally/' + encodeURIComponent(electionName)},
                    function (response) {
                        if (isSuccess(response.status)) {
                            showDetails(response.body)
                        }
                    }
                );
            }

            function getElectionExists() {
                sendRequest({
                        method: 'GET',
                        uri: '/elections/' + encodeURIComponent(electionName)},
                    function (response) {
                        if (isSuccess(response.status)) {
                            view.find('.election-exists-section').show();
                            getPlaces();
                        } else if (response.status == 404) {
                            view.find('.election-does-not-exist-section').show();
                        } else {
                            view.find('.problem-with-server-section').show();
                        }
                    });
            }

            function populateFloydWarshall(tally) {
                function composeInitialPaths() {
                    function composeInitialPathsWinner(winner) {
                        function composeInitialPathLoser(loser) {
                            if (winner === loser) return '';
                            else return '<tr><td>' + _.escape(winner) + '</td><td>' + tally.preferences[winner][loser] + '</td><td>' + _.escape(loser) + '</td></tr>'
                        }

                        return _.map(tally.candidateNames, composeInitialPathLoser).join('');
                    }

                    var initialPathsHeader = '<p>Initial paths</p>';
                    return initialPathsHeader + '<table>' + _.map(tally.candidateNames, composeInitialPathsWinner).join('') + '</table>';
                }

                function composeFloydWarshall(candidateName) {
                    function renderTable(rows) {
                        function renderRow(row) {
                            function renderCell(cell) {
                                return '<td>' + _.escape(cell) + '</td>'
                            }

                            return '<tr>' + _.map(row, renderCell).join('') + '</tr>'
                        }

                        return '<table>' + _.map(rows, renderRow).join('') + '</table>'
                    }

                    var alternativesHeader = '<p>Alternatives using candidate ' + _.escape(candidateName) + '</p>',
                        strongestPathsHeader = '<p>Strongest paths after resolving alternatives using ' + _.escape(candidateName) + '</p>';
                    return alternativesHeader + renderTable(tally.floydWarshall[candidateName].alternatives) + strongestPathsHeader + renderTable(tally.floydWarshall[candidateName].paths);
                }

                floydWarshallElement.html(
                    composeInitialPaths() +
                        _.map(tally.candidateNames, composeFloydWarshall).join(''));
            }

            function populateStrongestPaths(tally) {
                function composeStrongestPaths() {
                    function composeRows(winner) {
                        function composeRow(loser) {
                            if (winner === loser) return '';
                            else return '<tr>' + '<td>' + _.escape(winner) + '</td>' +
                                '<td>' + tally.strongestPaths[winner][loser] + '</td>' +
                                '<td>' + _.escape(loser) + '</td>' + '</tr>';
                        }

                        return _.map(tally.candidateNames, composeRow).join('');
                    }

                    return _.map(tally.candidateNames, composeRows).join('');
                }

                strongestPathsElement.html(composeStrongestPaths());
            }

            function populateStrongestPathsMatrix(tally) {
                function composeHeader() {
                    function composeCell(candidateName) {
                        return '<th>' + _.escape(candidateName) + '</th>'
                    }

                    return '<th></th>' + _.map(tally.candidateNames, composeCell).join('');
                }

                function composeRows() {
                    function composeRow(winner) {
                        function composeCell(loser) {
                            if (winner === loser) return '<td></td>';
                            else return '<td>' + tally.strongestPaths[winner][loser] + '</td>';
                        }

                        return '<tr><th>' + _.escape(winner) + '</th>' + _.map(tally.candidateNames, composeCell).join('') + '</tr>';
                    }

                    return _.map(tally.candidateNames, composeRow).join('');
                }

                strongestPathsHeaderElement.html(composeHeader());
                strongestPathsBodyElement.html(composeRows());
            }

            function showDetails(tally) {
                showPlaces(tally.places);
                view.find('.details-section').show();
                view.find('.hide-details').show();
                view.find('.show-details').hide();
                populateCandidateNames(tally.candidateNames);
                populateVotes(tally);
                populatePreferences(tally);
                populatePreferenceMatrix(tally);
                populateFloydWarshall(tally);
                populateStrongestPaths(tally);
                populateStrongestPathsMatrix(tally);
            }

            function hideDetails(tally) {
                view.find('.details-section').hide();
                view.find('.hide-details').hide();
                view.find('.show-details').show();
                populateCandidateNames(tally.candidateNames);
                populateVotes(tally);
                populatePreferences(tally);
                populatePreferenceMatrix(tally);
                populateFloydWarshall(tally);
                populateStrongestPaths(tally);
                populateStrongestPathsMatrix(tally);
            }

            function initialize() {
                view.find('.hide-details').hide();
                view.find('.election-name').text(electionName);
                view.find('.show-details').click(getTally);
                view.find('.hide-details').click(hideDetails);
                view.find('.election-page-link').attr('href', '../election/election.html?name=' + encodeURIComponent(electionName));
                view.find('.election-page-link').text('Election "' + electionName + '"');
                hideAll();
                getElectionExists();
            }

            tallyDetailsButton.click(function () {
                sendRequest({
                        method: 'GET',
                        uri: '/tally/' + encodeURIComponent(electionName)},
                    function (response) {
                        if (isSuccess(response.status)) {
                            showDetails(response.body)
                        }
                    }
                );
            });

            initialize();
        }

        return tallyPage;
    }
);
