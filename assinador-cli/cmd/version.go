package cmd

import (
	"fmt"

	"github.com/spf13/cobra"
)

var Version = "1.0.0"

var versionCmd = &cobra.Command{
	Use:   "version",
	Short: "Exibe a versão da aplicação",
	Run: func(cmd *cobra.Command, args []string) {
		fmt.Println("assinatura CLI versão:", Version)
	},
}

func init() {
	rootCmd.AddCommand(versionCmd)
}