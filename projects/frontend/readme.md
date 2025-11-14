## To use react components in clojurescript
Babel
Use this command to translate the jsx, tsx, ts code to js (you can adapt it later to your project)
Open a terminal and run the following command:
npx babel ./client/src --out-dir ./resources/js --extensions ".ts,.tsx,.jsx" --ignore "node_modules" --watch
npx esbuild "client/src/**/*.ts" "client/src/**/*.tsx" --bundle --outdir=resources/js --format=esm --target=es2018 --watch
Note: --target and --bundle to avoid issues encontered

## To generate the index.css with tailwind
npx tailwindcss -i ../../bases/frontend/ui/index.css -o ./resources/public/css/index.css --watch