const path = require('path');
module.exports = {
  parser: "vue-eslint-parser",
  parserOptions: {
    parser: {
      ts: "@typescript-eslint/parser",
    },
    ecmaVersion: 2022,
    sourceType: "module",
    createDefaultProgram: true,
    project: "tsconfig.json",
    tsconfigRootDir: __dirname,
    extraFileExtensions: [".vue"],
    vueFeatures: {
      filter: false,
      interpolationAsNonHTML: true,
      styleCSSVariableInjection: true,
    },
  },
  env: {
    "vue/setup-compiler-macros": true,
    browser: true,
    es2024: true,
    node: true,
  },
  extends: [
    "airbnb-base",
    "eslint:recommended",
    "@vue/typescript/recommended",
    "plugin:vue/base",
    "plugin:vue/vue3-strongly-recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:testing-library/vue",
    "plugin:import/typescript",
    "plugin:import/recommended",
    "plugin:prettier/recommended",
  ],
  ignorePatterns: [
    ".git",
    ".github",
    "dist",
    "node_modules",
    "*.md",
    "index.html",
    "components.d.ts",
    "index.d.ts",
    "shims-vue.d.ts",
    "tsconfig.json",
    ".eslintrc.cjs",
    "vite.config.ts",
    "tests/setupVitest.js",
  ],
  settings: {
    "import/extensions": [".ts", ".tsx"],
    "import/resolver": {
      typescript: {
	project: "./tsconfig.json"
      },
      alias: {
        map: [["@", path.resolve(__dirname,"./src")]],
        extensions: [".js", ".ts", ".d.ts", ".vue"],
      },
    },
  },
  plugins: [
    "vue",
    "testing-library",
    "@typescript-eslint",
    "vitest",
    "prettier",
    "@stylistic",
  ],
  rules: {
    "no-unused-vars": ["error", { varsIgnorePattern: ".*", args: "none" }],
    "no-param-reassign": ["error", { props: false }],
    "no-return-assign": ["error", "except-parens"],
    "no-useless-return": "off",
    "no-console": process.env.NODE_ENV === "production" ? "error" : "warn",
    "no-debugger": process.env.NODE_ENV === "production" ? "error" : "warn",
    "linebreak-style": ["error", "unix"],
    "import/extensions": "off",
    "import/no-self-import": "error",
    "import/no-cycle": ["error", { maxDepth: 1, ignoreExternal: true }],
    "testing-library/await-async-queries": "error",
    "testing-library/no-await-sync-queries": "error",
    "testing-library/no-debugging-utils": "warn",
    "testing-library/no-dom-import": "off",
    "vue/multi-word-component-names": 0,
    "prettier/prettier": "off",
    "@stylistic/semi": ["error", "never"],
  },
  overrides: [
    {
      files: ["*.json"],
      rules: {
        "no-unused-expressions": "off",
      },
    },
    {
      files: ["*.config.*", ".*.js"],
      rules: {
        "import/no-extraneous-dependencies": "off",
      },
    },
    {
      files: ["**/*.vue"],
      parser: "vue-eslint-parser",
      rules: {
        "no-undef": "off",
      },
    },
    {
      files: ["**/*.ts"],
      parser: "@typescript-eslint/parser",
      rules: {
        "no-undef": "off",
        "no-use-before-define": "off",
      },
    },
    {
      files: ["**/*.vue"],
      rules: {
        "@typescript-eslint/no-unused-vars": "off",
        "vue/require-default-prop": "off",
        "vue/v-bind-style": "off",
        "vue/no-template-shadow": "off",
      },
    },
  ],
};
