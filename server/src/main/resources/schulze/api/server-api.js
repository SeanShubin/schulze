define([],
    function ($, _) {
        'use strict';
        return function (jsonOverHttp) {
            var listener;

            function logError(message) {
                console.log('ERROR: ' + message);
            }

            function setListener(newListener) {
                listener = newListener;
            }

            function isSuccess(response) {
                return response.status >= 200 && response.status < 400;
            }

            //voters
            function respondWithVoters(response) {
                if (isSuccess(response)) {
                    listener.notifyRegardingVoters(response.body);
                } else {
                    logError('unable to get voters');
                }
            }

            function interestedInVoters() {
                jsonOverHttp({uri: '/voters', method: 'GET'}, respondWithVoters);
            }

            //voter
            function respondWithVoterExistence(response) {
                if (isSuccess(response)) {
                    listener.notifyRegardingVoterExistence(true);
                } else if (response.status === 404) {
                    listener.notifyRegardingVoterExistence(false);
                } else {
                    logError('unable to determine if voter exists');
                }
            }

            function interestedInVoterExistence(voterName) {
                jsonOverHttp({
                    uri: '/voters/' + encodeURIComponent(voterName),
                    method: 'GET'
                }, respondWithVoterExistence);
            }

            function addVoter(voterName) {
                jsonOverHttp({
                    uri: '/voters',
                    method: 'POST',
                    body: {name: voterName}
                }, interestedInVoters);
            }

            function deleteVoter(voterName) {
                function invokeInterestedInVoterExistence() {
                    interestedInVoterExistence(voterName);
                }

                jsonOverHttp({
                    uri: '/voters/' + encodeURIComponent(voterName),
                    method: 'DELETE'
                }, invokeInterestedInVoterExistence);
            }

            //elections
            function respondWithElections(response) {
                if (isSuccess(response)) {
                    listener.notifyRegardingElections(response.body);
                }
            }

            function interestedInElections() {
                jsonOverHttp({uri: '/elections', method: 'GET'}, respondWithElections);
            }

            //election
            function respondWithElectionExistence(response) {
                if (isSuccess(response)) {
                    listener.notifyRegardingElectionExistence(true);
                } else if (response.status === 404) {
                    listener.notifyRegardingElectionExistence(false);
                } else {
                    logError('unable to determine if election exists');
                }
            }

            function interestedInElectionExistence(electionName) {
                jsonOverHttp({
                    uri: '/elections/' + encodeURIComponent(electionName),
                    method: 'GET'
                }, respondWithElectionExistence);
            }

            function addElection(electionName) {
                jsonOverHttp({
                    uri: '/elections',
                    method: 'POST',
                    body: {name: electionName}
                }, interestedInElections);
            }

            function deleteElection(electionName) {
                function invokeInterestedInElectionExistence() {
                    interestedInElectionExistence(electionName);
                }

                jsonOverHttp({
                    uri: '/elections/' + encodeURIComponent(electionName),
                    method: 'DELETE'
                }, invokeInterestedInElectionExistence);
            }

            //candidates
            function respondWithCandidates(response) {
                if (isSuccess(response)) {
                    listener.notifyRegardingCandidates(response.body);
                } else {
                    logError('unable to get candidates');
                }
            }

            function interestedInCandidates(electionName) {
                jsonOverHttp({
                    uri: '/elections/' + encodeURIComponent(electionName) + '/candidates',
                    method: 'GET'
                }, respondWithCandidates);
            }

            function addCandidate(options) {
                function notifyCandidatesChanged() {
                    interestedInCandidates(options.electionName);
                }

                jsonOverHttp({
                    uri: '/elections/' + encodeURIComponent(options.electionName) + '/candidates',
                    method: 'POST',
                    body: {name: options.candidateName}
                }, notifyCandidatesChanged);
            }

            //candidate
            function respondWithCandidateExistence(response) {
                if (isSuccess(response)) {
                    listener.notifyRegardingCandidateExistence(true);
                } else if (response.status === 404) {
                    listener.notifyRegardingCandidateExistence(false);
                } else {
                    logError('unable to determine if candidate exists');
                }
            }

            function interestedInCandidateExistence(options) {
                jsonOverHttp({
                    uri: '/elections/' + encodeURIComponent(options.electionName) +
                        '/candidates/' + encodeURIComponent(options.candidateName),
                    method: 'GET'
                }, respondWithCandidateExistence);
            }

            function deleteCandidate(options) {
                function invokeInterestedInCandidateExistence() {
                    interestedInCandidateExistence(options);
                }

                jsonOverHttp({
                    uri: '/elections/' + encodeURIComponent(options.electionName) +
                        '/candidates/' + encodeURIComponent(options.candidateName),
                    method: 'DELETE'
                }, invokeInterestedInCandidateExistence);
            }

            //vote
            function notifyRegardingVote(response) {
                listener.notifyRegardingVote(response.body);
            }

            function interestedInVote(options) {
                jsonOverHttp({
                    uri: '/vote?voter=' + encodeURIComponent(options.voterName) + '&election=' + encodeURIComponent(options.electionName),
                    method: 'GET'
                }, notifyRegardingVote);
            }

            function setVote(options) {
                var invokeInterestedInVote = function () {
                    interestedInVote({voterName: options.voterName, electionName: options.electionName});
                };
                jsonOverHttp({
                    uri: '/vote?voter=' +
                        encodeURIComponent(options.voterName) +
                        '&election=' +
                        encodeURIComponent(options.electionName),
                    method: 'PUT',
                    body: options.rankings
                }, invokeInterestedInVote);
            }

            //tally
            function notifyRegardingTally(response) {
                listener.notifyRegardingTally(response.body);
            }

            function interestedInTally(electionName) {
                jsonOverHttp({
                    uri: '/tally/' + encodeURIComponent(electionName),
                    method: 'GET'
                }, notifyRegardingTally);
            }

            return {
                setListener: setListener,
                interestedInElections: interestedInElections,
                interestedInVoterExistence: interestedInVoterExistence,
                deleteVoter: deleteVoter,
                interestedInElectionExistence: interestedInElectionExistence,
                deleteElection: deleteElection,
                interestedInVoters: interestedInVoters,
                addVoter: addVoter,
                addElection: addElection,
                interestedInCandidates: interestedInCandidates,
                addCandidate: addCandidate,
                interestedInCandidateExistence: interestedInCandidateExistence,
                deleteCandidate: deleteCandidate,
                interestedInVote: interestedInVote,
                setVote: setVote,
                interestedInTally: interestedInTally
            };
        };
    });
