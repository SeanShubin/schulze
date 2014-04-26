define(['underscore'], function (_) {
    'use strict';
    function keyValueStringToPair(keyValueString) {
        var parts;
        parts = keyValueString.split('=');
        return {
            key: decodeURIComponent(parts[0]),
            value: decodeURIComponent(parts[1])
        }
    }

    function queryToKeyValueArray(query) {
        var keyValueStrings, keyValuePairs;
        keyValueStrings = query.split('&');
        keyValuePairs = _.map(keyValueStrings, keyValueStringToPair);
        return keyValuePairs;
    }

    function keyValueArrayToMapFirstOnly(keyValueArray) {
        var map;
        function updateMap(mapSoFar, keyValue) {
            if (mapSoFar.hasOwnProperty(keyValue.key)) {
                return mapSoFar;
            } else {
                mapSoFar[keyValue.key] = keyValue.value;
                return mapSoFar;
            }
        }
        map = _.foldl(keyValueArray, updateMap, {});
        return map;
    }

    function parseUrl(text) {
        if (text === undefined) {
            throw 'parseUrl requires a string parameter';
        }
        var urlRegex, scheme, domain, port, path, query, fragment, parts, queryParts, queryValuesFirstOnly, result;
        scheme = '(?:([^:]*)://)?';
        domain = '([^:/]*)?';
        port = '(?::([^/?#]*))?';
        path = '(?:(/[^?#]*))?';
        query = '(?:\\?([^#]*))?';
        fragment = '(?:#(.*))?';
        urlRegex = new RegExp(scheme + domain + port + path + query + fragment);
        parts = urlRegex.exec(text);
        queryParts = queryToKeyValueArray(parts[5]);
        queryValuesFirstOnly = keyValueArrayToMapFirstOnly(queryParts);
        result = {
            scheme: parts[1],
            domain: parts[2],
            port: parts[3],
            path: parts[4],
            query: parts[5],
            fragment: parts[6],
            queryParts: queryParts,
            queryValuesFirstOnly: queryValuesFirstOnly
        };
        return result;
    }

    return parseUrl;
});
