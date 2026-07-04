# Old Jenkins build marker in git history

`master` is fine. Metrics code is gone.

Full history scans (gitleaks, etc.) still flag a 32-char hex string in old commits (~2013 through metrics removal). That is not a bStats API key. It was a hardcoded value compared against a `.jenkins` resource so the old mcStats graph could report whether a build came from official CI.

See `8aabe1c14` in `mcMMO.java` if you want the original context.

## Do you need to do anything?

Probably not. Nothing on `master` reads or sends it. There is no bStats credential to rotate.

If you want a clean `gitleaks detect --log-opts="--all"` on a mirror clone, an admin can:

1. Mirror clone `mcMMO-Dev/mcMMO`
2. Run `scripts/purge-metrics-official-key-history.sh`
3. Confirm gitleaks clean
4. `git push --mirror --force` and tell contributors to re-clone

Normal merge does not rewrite history. This PR is docs + a helper script only.
