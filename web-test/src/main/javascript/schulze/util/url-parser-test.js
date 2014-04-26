require(['jquery', 'underscore', '/schulze/util/url-parser.js'], function ($, _, parseUrl) {
    module('url parser');
    test('parse url', function () {
        var
            url = 'scheme://domain:12345/path?aaa=bbb&ccc=ddd&aaa=eee#fragment',
            parsed = parseUrl(url);
        equal('scheme', parsed.scheme, 'scheme');
        equal('domain', parsed.domain, 'domain');
        equal('12345', parsed.port, 'port');
        equal('/path', parsed.path, 'path');
        equal('aaa=bbb&ccc=ddd&aaa=eee', parsed.query, 'query');
        equal('fragment', parsed.fragment, 'fragment');
        equal(3, parsed.queryParts.length, 'query part count');
        equal('aaa', parsed.queryParts[0].key, 'query part key 0');
        equal('bbb', parsed.queryParts[0].value, 'query part value 0');
        equal('ccc', parsed.queryParts[1].key, 'query part key 1');
        equal('ddd', parsed.queryParts[1].value, 'query part value 1');
        equal('aaa', parsed.queryParts[2].key, 'query part key 2');
        equal('eee', parsed.queryParts[2].value, 'query part value 2');
        equal('bbb', parsed.queryValuesFirstOnly['aaa'], 'query value 1');
        equal('ddd', parsed.queryValuesFirstOnly['ccc'], 'query value 2');
    });
});
