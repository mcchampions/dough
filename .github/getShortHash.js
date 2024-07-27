const fs = require('fs');

let commit = process.env.GITHUB_SHA.substring(0,10);
fs.writeFileSync(process.env.GITHUB_ENV, "shorthash=" + commit);
