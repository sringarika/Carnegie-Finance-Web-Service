module.exports = {
  parsePortfolio: parsePortfolio,
  initFundPrices: initFundPrices,
  fillInPrice: fillInPrice,
  validateTransitionDay: validateTransitionDay,
  rememberSession: rememberSession,
  reuseSession: reuseSession,
};

function fillInPrice(requestParams, context, ee, next) {
  var jsonStr = JSON.stringify(requestParams.json);
  var i = context.vars.$loopCount;
  var price = context.vars.$price[i];
  jsonStr = jsonStr.replace(/\$price-i/g, '' + price);
  jsonStr = jsonStr.replace(/\$i/g, '' + i);
  requestParams.json = JSON.parse(jsonStr);
  return next();
}

function initFundPrices(requestParams, response, context, ee, next) {
  if (context.vars.$price) {
    return next(new Error('Cannot initFundPrices twice!'));
  }
  var prices = context.vars.$price = [];
  for (var i = 0; i < 100; i++) {
    var price = Math.ceil(Math.random() * 10000) / 100;
    if (price < 0.01) price = 0.01;
    prices.push(price);
  }
  return next();
}

function validateTransitionDay(requestParams, response, context, ee, next) {
  parsePortfolio(requestParams, response, context, ee, function (err) {
    if (err) return next(err);
    for (var i = 0; i < 100; i++) {
      var oldPrice = context.vars.$price[i];
      var newPrice = parseFloat(response.body.$price['test-' + i]);
      if (newPrice <= 0) {
        return next(new Error('Price for test-' + i + ' became zero!'));
      }
      var percent = Math.round(newPrice * 100 / oldPrice);
      if (percent < 90) {
        return next(new Error('Price for test-' + i + ' decreased more than 10%!'));
      } else if (percent > 110) {
        return next(new Error('Price for test-' + i + ' increased more than 10%!'));
      }
      context.vars.$price[i] = newPrice;
    }
    response.body.$valid = true;
    return next();
  });
}

function parsePortfolio(requestParams, response, context, ee, next) {
  var portfolio = response.body;
  if (typeof portfolio === 'string') {
    portfolio = JSON.parse(portfolio);
  }
  if (portfolio.funds) {
    var shares = portfolio.$shares = {};
    var price = portfolio.$price = {};
    portfolio.funds.forEach(function (fund) {
      shares[fund.name] = fund.shares;
      price[fund.name] = fund.price;
    });
    response.body = portfolio;
  }
  return next();
}

var sessionId = null;
var sessionCookieName = 'CFSAuthToken';
var fs = require('fs');

function rememberSession(requestParams, response, context, ee, next) {
  var cookies = response.headers['set-cookie'];
  sessionId = null;
  cookies.forEach(function (cookie) {
    var parts = cookie.split('=');
    if (parts[0] == sessionCookieName) {
      sessionId = parts[1].split(';')[0]
    }
  });
  if (!sessionId) {
    return next(new Error("Cannot find cookie with name: " + sessionCookieName));
  }
  fs.writeFileSync('.tmp-sessionid', sessionId);
  return next();
}

function reuseSession(requestParams, context, ee, next) {
  if (!sessionId) {
    sessionId = fs.readFileSync('.tmp-sessionid', {encoding: 'utf8'});
  }
  if (!sessionId) return next(new Error("No session remembered!"));
  requestParams.headers = requestParams.headers || {};
  requestParams.headers['cookie'] = sessionCookieName + '=' + sessionId;
  return next();
}
