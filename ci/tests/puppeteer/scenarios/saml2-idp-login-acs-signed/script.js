const puppeteer = require('puppeteer');
const path = require('path');
const cas = require('../../cas.js');
const assert = require("assert");

async function cleanUp() {
    await cas.removeDirectory(path.join(__dirname, '/saml-md'));
    await cas.removeDirectory(path.join(__dirname, '/saml-sp'));
}

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    const response = await cas.goto(page, "https://localhost:8443/cas/idp/metadata");
    console.log(`${response.status()} ${response.statusText()}`);
    assert(response.ok());
    
    await cas.waitFor('https://localhost:9876/sp/saml/status', async () => {
        console.log("Trying without an exising SSO session...");
        await cas.goto(page, "https://localhost:9876/sp");
        await page.waitForTimeout(3000);
        await page.waitForSelector('#idpForm', {visible: true});
        await cas.submitForm(page, "#idpForm");
        await page.waitForTimeout(2000);

        await page.waitForSelector('#username', {visible: true});
        await cas.loginWith(page, "casuser", "Mellon");
        await page.waitForResponse(response => response.status() === 200);
        await page.waitForTimeout(3000);
        console.log(`Page URL: ${page.url()}`);
        await page.waitForSelector('body pre', { visible: true });
        let content = await cas.textContent(page, "body pre");
        let payload = JSON.parse(content);
        console.log(payload);
        assert(payload.form.SAMLResponse !== null);
        console.log("Trying with an exising SSO session...");
        await cas.goto(page, "https://localhost:8443/cas/logout");
        await cas.goto(page, "https://localhost:8443/cas/login");
        await cas.loginWith(page, "casuser", "Mellon");
        await cas.assertCookie(page);
        await cas.goto(page, "https://localhost:9876/sp");
        await page.waitForTimeout(3000);
        await page.waitForSelector('#idpForm', {visible: true});
        await cas.submitForm(page, "#idpForm");
        await page.waitForTimeout(3000);
        console.log(`Page URL: ${page.url()}`);
        await page.waitForSelector('body pre', { visible: true });
        content = await cas.textContent(page, "body pre");
        payload = JSON.parse(content);
        console.log(payload);
        assert(payload.form.SAMLResponse !== null);

        await browser.close();
        await cleanUp();
        process.exit()
    }, async error => {
        await cleanUp();
        console.log(error);
        throw error;
    })
})();

