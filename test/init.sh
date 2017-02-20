#!/bin/bash

for file in $(ls init-*.json | sort); do
  echo "========== Running $file ============="
  DEBUG="" artillery run "$file" || exit 1;
done;
