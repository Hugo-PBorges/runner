package cmd

import (
	"fmt"
	"runtime/debug"

	"github.com/spf13/cobra"
)

// Version é sobrescrito em build time via -ldflags "-X main.Version=x.y.z".
var Version = "dev"

var versionCmd = &cobra.Command{
	Use:   "version",
	Short: "Exibe a versão e o commit do build",
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Printf("simulador-cli %s (%s)\n", Version, simBuildCommit())
	},
}

func simBuildCommit() string {
	info, ok := debug.ReadBuildInfo()
	if !ok {
		return "unknown"
	}
	for _, s := range info.Settings {
		if s.Key == "vcs.revision" {
			if len(s.Value) > 7 {
				return s.Value[:7]
			}
			return s.Value
		}
	}
	return "unknown"
}

func init() {
	rootCmd.AddCommand(versionCmd)
	rootCmd.Version = Version
}
