module.exports = {
  parsePortfolio: parsePortfolio,
};

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
