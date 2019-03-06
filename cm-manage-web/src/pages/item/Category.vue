<template>
  <v-card><!--卡片效果，包裹然后产生一个悬浮效果 -->
      <v-flex xs12 sm10><!--栅格的一行-->
        <!--与config.js里面baseUrl拼接
        这里虽然标签是url，但是底层是调用ajax发起跨域请求
        在componnents.tree里面有两个自定义组件，就是这个树组件
        -->
        <v-tree ref="tree"  url="/item/category/list"
                :isEdit="isEdit"
                @handleAdd="handleAdd"
                @handleEdit="handleEdit"
                @handleDelete="handleDelete"
                @handleClick="handleClick"
        />
      </v-flex>
  </v-card>
</template>

<script>
  export default {
    name: "category",
    data() {
      return {
        //treeData: treeData,
        isEdit:true,
        leafNode:[],
      }
    },
    methods: {
      handleAdd(node) {
        console.log("add .... ");
        console.log(node);
      },
      handleEdit(id, name) {
        console.log("edit... id: " + id + ", name: " + name)
      },
      handleDelete(id) {
        console.log("delete ... " + id)
      },
      handleClick(node) {
        console.log(node)
      },
      reloadData(id){
        //操作完成后刷新数据
        this.$http.get("/item/category/list?pid="+id).then().catch();
      }
    }
  };
</script>

<style scoped>

</style>
