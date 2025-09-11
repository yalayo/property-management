npm i # install NPM deps
npm run dev # run dev build in watch mode with CLJS REPL
npx wrangler dev # run Cloudflare server at http://localhost:8787

## Local database
# Initialize migrations
npx wrangler d1 migrations create landlord-db (file name)

# Run the migrations locally
npx wrangler d1 migrations apply landlord-db --local

Note:
Command to see all the tables
npx wrangler d1 execute landlord-db --local --command "SELECT name FROM sqlite_master WHERE type='table';"